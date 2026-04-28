package com.ruoyi.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * 3D模型格式转换工具类
 * 调用Python脚本将STP/STEP文件转换为GLTF格式
 */
@Component
public class ModelConversionUtils {

    private static final Logger log = LoggerFactory.getLogger(ModelConversionUtils.class);

    private static final long CONVERT_TIMEOUT = 180;

    @Value("${ruoyi.python-path:python}")
    private String pythonPath;

    @Value("${ruoyi.model-3d-path:/model/3d}")
    private String model3dPath;

    /**
     * 将STP/STEP文件转换为GLTF格式
     *
     * @param stpFilePath  STP文件绝对路径
     * @param gltfFilePath GLTF输出文件绝对路径
     * @return 转换是否成功
     */
    public boolean convertStpToGltf(String stpFilePath, String gltfFilePath) {
        String scriptPath = getScriptPath();
        File scriptFile = new File(scriptPath);

        if (!scriptFile.exists()) {
            log.error("转换脚本不存在: {}", scriptPath);
            return false;
        }

        ProcessBuilder pb = new ProcessBuilder(
                pythonPath,
                scriptFile.getAbsolutePath(),
                stpFilePath,
                gltfFilePath
        );

        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            boolean finished = process.waitFor(CONVERT_TIMEOUT, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                log.error("3D模型转换超时(>{}秒): {}", CONVERT_TIMEOUT, stpFilePath);
                return false;
            }

            int exitCode = process.exitValue();
            if (exitCode == 0) {
                log.info("3D模型转换成功: {} -> {}", stpFilePath, gltfFilePath);
                return true;
            } else {
                log.error("3D模型转换失败(exit={}): {}\n{}", exitCode, stpFilePath, output);
                return false;
            }

        } catch (IOException e) {
            log.error("3D模型转换进程启动失败: {}", e.getMessage());
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("3D模型转换进程被中断");
            return false;
        }
    }

    /**
     * 获取转换脚本的路径
     */
    private String getScriptPath() {
        String userDir = System.getProperty("user.dir");
        File scriptInUserDir = new File(userDir, "../tools/convert_stp_to_gltf.py");
        if (scriptInUserDir.exists()) {
            return scriptInUserDir.getAbsolutePath();
        }
        // 回退：尝试项目根目录下的 tools
        File scriptInRoot = new File("tools/convert_stp_to_gltf.py");
        if (scriptInRoot.exists()) {
            return scriptInRoot.getAbsolutePath();
        }
        return "tools/convert_stp_to_gltf.py";
    }

    public String getModel3dPath() {
        return model3dPath;
    }
}
