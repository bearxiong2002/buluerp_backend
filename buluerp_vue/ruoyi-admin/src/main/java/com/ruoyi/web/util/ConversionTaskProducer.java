package com.ruoyi.web.util;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.redis.RedisCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 3D模型转换任务生产者
 * 将转换任务推入 Redis List 队列
 */
@Component
public class ConversionTaskProducer {

    private static final Logger log = LoggerFactory.getLogger(ConversionTaskProducer.class);

    /** Redis 队列 key */
    public static final String QUEUE_KEY = "stp:conversion:queue";

    @Autowired
    private RedisCache redisCache;

    /**
     * 将转换任务推入队列尾部
     */
    public void push(ConversionTask task) {
        String json = JSON.toJSONString(task);
        redisCache.setCacheList(QUEUE_KEY, Collections.singletonList(json));
        log.info("已推送3D模型转换任务: materialId={}", task.getMaterialId());
    }
}
