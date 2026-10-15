package com.ruoyi.web.util;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.config.ServerConfig;
import com.ruoyi.web.mapper.ErpMaterialInfoMapper;
import com.ruoyi.web.websocket.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 3D模型转换任务消费者
 * 从 Redis List 队列弹出任务并执行 STP→GLTF 转换
 */
@Component
public class ConversionTaskConsumer implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ConversionTaskConsumer.class);

    /** 队列为空时的轮询间隔（毫秒） */
    private static final long POLL_INTERVAL_MS = 2000;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ModelConversionUtils conversionUtils;

    @Autowired
    private ErpMaterialInfoMapper materialMapper;

    @Autowired
    private ServerConfig serverConfig;

    /** 控制消费者运行状态 */
    private volatile boolean running = true;

    @Override
    public void run() {
        log.info("3D模型转换消费者启动，监听队列: {}", ConversionTaskProducer.QUEUE_KEY);
        while (running) {
            try {
                String taskJson = redisCache.rightPop(ConversionTaskProducer.QUEUE_KEY);
                if (taskJson == null) {
                    Thread.sleep(POLL_INTERVAL_MS);
                    continue;
                }
                ConversionTask task = JSON.parseObject(taskJson, ConversionTask.class);
                processTask(task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("消费3D模型转换任务异常", e);
            }
        }
        log.info("3D模型转换消费者已停止");
    }

    /**
     * 执行单个转换任务
     */
    private void processTask(ConversionTask task) {
        log.info("开始转换3D模型: materialId={}, stp={}", task.getMaterialId(), task.getStpAbsPath());
        try {
            boolean converted = conversionUtils.convertStpToGltf(
                    task.getStpAbsPath(), task.getGltfAbsPath());

            if (converted) {
                updateMaterialModelUrl(task);
                notifyUser(task);
                log.info("3D模型转换成功: materialId={}", task.getMaterialId());
            } else {
                log.error("3D模型转换失败（Python脚本执行失败或超时）: materialId={}, stp={}",
                        task.getMaterialId(), task.getStpAbsPath());
            }
        } catch (Exception e) {
            log.error("3D模型转换异常: materialId={}", task.getMaterialId(), e);
        }
    }

    /**
     * 更新物料的 model_url 字段
     */
    private void updateMaterialModelUrl(ConversionTask task) {
        try {
            String gltfFileName = new java.io.File(task.getGltfAbsPath()).getName();
            String gltfUrlPath = StringUtils.substringBeforeLast(task.getStpUrlPath(), "/")
                    + "/" + gltfFileName;
            String gltfUrl = serverConfig.getUrl() + gltfUrlPath;
            materialMapper.updateModelUrl(task.getMaterialId(), gltfUrl);
            log.info("已更新物料 model_url: materialId={}, url={}", task.getMaterialId(), gltfUrl);
        } catch (Exception e) {
            log.error("更新物料 model_url 失败: materialId={}", task.getMaterialId(), e);
        }
    }

    /**
     * 通过 WebSocket 通知用户转换完成
     */
    private void notifyUser(ConversionTask task) {
        if (task.getUserId() != null) {
            WebSocketServer.sendMessageToUser(task.getUserId(),
                    "3D模型转换完成，物料ID：" + task.getMaterialId());
        }
    }

    /**
     * 停止消费者
     */
    public void stop() {
        this.running = false;
    }
}
