package com.ruoyi.web.service.support;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** 沿用系统上传目录和 /profile 地址；文件清理与模具事务结果保持一致。 */
public final class MouldImages {
    private static final Logger log = LoggerFactory.getLogger(MouldImages.class);

    private MouldImages() { }

    public static String upload(MultipartFile picture) {
        try {
            String url = FileUploadUtils.upload(RuoYiConfig.getUploadPath(), picture, MimeTypeUtils.IMAGE_EXTENSION);
            deleteAfterRollback(url);
            return url;
        } catch (Exception e) {
            throw new ServiceException("模具图片上传失败：" + e.getMessage());
        }
    }

    public static void deleteAfterRollback(String url) {
        if (StringUtils.isBlank(url) || !TransactionSynchronizationManager.isSynchronizationActive()) return;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) deleteStoredFile(url);
            }
        });
    }

    public static void deleteAfterCommit(String url) {
        if (StringUtils.isBlank(url)) return;
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            deleteStoredFile(url);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() { deleteStoredFile(url); }
        });
    }

    private static void deleteStoredFile(String url) {
        if (StringUtils.isBlank(url) || !url.startsWith(Constants.RESOURCE_PREFIX + "/")) return;
        try {
            Path profile = Paths.get(RuoYiConfig.getProfile()).toAbsolutePath().normalize();
            Path file = profile.resolve(url.substring((Constants.RESOURCE_PREFIX + "/").length())).normalize();
            if (file.startsWith(profile) && Files.isRegularFile(file)) Files.deleteIfExists(file);
        } catch (Exception e) {
            log.warn("清理模具图片失败：{}", url, e);
        }
    }
}
