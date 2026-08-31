package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.file.InvalidExtensionException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.web.domain.ErpMaterialType;
import com.ruoyi.web.mapper.ErpMaterialTypeMapper;
import com.ruoyi.web.service.IErpMaterialInfoService;
import com.ruoyi.web.service.IErpMaterialTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Service
public class ErpMaterialTypeServiceImpl
        extends ServiceImpl<ErpMaterialTypeMapper, ErpMaterialType>
        implements IErpMaterialTypeService {
    private static final Logger log = LoggerFactory.getLogger(ErpMaterialTypeServiceImpl.class);

    @Autowired
    private IErpMaterialInfoService erpMaterialInfoService;

    @Override
    public void checkUnique(ErpMaterialType erpMaterialType) {
        if (StringUtils.isNotBlank(erpMaterialType.getName())
                && StringUtils.isNotBlank(erpMaterialType.getColorCode())) {
            LambdaQueryWrapper<ErpMaterialType> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ErpMaterialType::getName, erpMaterialType.getName());
            queryWrapper.eq(ErpMaterialType::getColorCode, erpMaterialType.getColorCode());
            ErpMaterialType materialType = this.baseMapper.selectOne(queryWrapper);
            if (materialType != null && !Objects.equals(materialType.getId(), erpMaterialType.getId())) {
                throw new ServiceException("料型名称和颜色编号已存在");
            }
        }
    }

    @Override
    public void check(ErpMaterialType materialType) {
        checkUnique(materialType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveChecked(ErpMaterialType materialType) throws IOException {
        check(materialType);
        String uploadedUrl = null;
        try {
            if (materialType.getReferenceColor() != null && !materialType.getReferenceColor().isEmpty()) {
                uploadedUrl = uploadReferenceColor(materialType);
                deleteStoredFileAfterRollback(uploadedUrl);
            }
            return this.save(materialType);
        } catch (RuntimeException | IOException e) {
            deleteStoredFile(uploadedUrl);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateChecked(ErpMaterialType materialType) throws IOException {
        check(materialType);
        ErpMaterialType original = this.getById(materialType.getId());
        if (original == null) {
            throw new ServiceException("物料类型不存在");
        }

        String uploadedUrl = null;
        boolean deleteReferenceColor = Boolean.TRUE.equals(materialType.getDeleteReferenceColor());
        try {
            if (materialType.getReferenceColor() != null && !materialType.getReferenceColor().isEmpty()) {
                uploadedUrl = uploadReferenceColor(materialType);
                deleteStoredFileAfterRollback(uploadedUrl);
                deleteReferenceColor = false;
            } else {
                // 不接受客户端直接改写图片URL；未上传新图时保持原值。
                materialType.setReferenceColorUrl(null);
            }

            boolean updated = this.updateById(materialType);
            if (updated && deleteReferenceColor) {
                updated = this.lambdaUpdate()
                        .eq(ErpMaterialType::getId, materialType.getId())
                        .set(ErpMaterialType::getReferenceColorUrl, null)
                        .update();
            }
            if (updated && (uploadedUrl != null || deleteReferenceColor)) {
                deleteStoredFileAfterCommit(original.getReferenceColorUrl());
            }
            return updated;
        } catch (RuntimeException | IOException e) {
            deleteStoredFile(uploadedUrl);
            throw e;
        }
    }

    private String uploadReferenceColor(ErpMaterialType materialType) throws IOException {
        try {
            String url = FileUploadUtils.upload(
                    RuoYiConfig.getUploadPath(),
                    materialType.getReferenceColor(),
                    MimeTypeUtils.IMAGE_EXTENSION
            );
            materialType.setReferenceColorUrl(url);
            return url;
        } catch (InvalidExtensionException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    private void deleteStoredFile(String url) {
        if (StringUtils.isBlank(url) || !url.startsWith(Constants.RESOURCE_PREFIX)) {
            return;
        }
        try {
            Path profilePath = Paths.get(RuoYiConfig.getProfile()).toAbsolutePath().normalize();
            String relativePath = url.substring(Constants.RESOURCE_PREFIX.length())
                    .replaceFirst("^[\\\\/]+", "");
            Path filePath = profilePath.resolve(relativePath).normalize();
            if (filePath.startsWith(profilePath)) {
                Files.deleteIfExists(filePath);
            }
        } catch (Exception e) {
            log.warn("删除料型参考颜色图片失败，URL: {}", url, e);
        }
    }

    private void deleteStoredFileAfterCommit(String url) {
        if (StringUtils.isBlank(url)) {
            return;
        }
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    deleteStoredFile(url);
                }
            });
        } else {
            deleteStoredFile(url);
        }
    }

    private void deleteStoredFileAfterRollback(String url) {
        if (StringUtils.isBlank(url) || !TransactionSynchronizationManager.isActualTransactionActive()) {
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status != TransactionSynchronization.STATUS_COMMITTED) {
                    deleteStoredFile(url);
                }
            }
        });
    }

    @Override
    public void removeChecked(Long id) {
        ErpMaterialType erpMaterialType = this.getById(id);
        if (erpMaterialType == null) {
            throw new ServiceException("物料类型不存在");
        }
        if (!CollectionUtils.isEmpty(erpMaterialInfoService.listByMaterialTypeAndColorCode(
                erpMaterialType.getName(), erpMaterialType.getColorCode()))) {
            throw new ServiceException("物料类型已被使用，不能删除");
        }
        if (!this.removeById(id)) {
            throw new ServiceException("删除失败");
        }
        deleteStoredFileAfterCommit(erpMaterialType.getReferenceColorUrl());
    }

    @Override
    @Transactional
    public void removeBatchChecked(List<Long> ids) {
        for (Long id : ids) {
            removeChecked(id);
        }
    }

    @Override
    public ErpMaterialType getByName(String name) {
        return getByNameAndColorCode(name, null);
    }

    @Override
    public ErpMaterialType getByNameAndColorCode(String name, String colorCode) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        LambdaQueryWrapper<ErpMaterialType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ErpMaterialType::getName, name);
        if (StringUtils.isNotBlank(colorCode)) {
            queryWrapper.eq(ErpMaterialType::getColorCode, colorCode);
        }
        List<ErpMaterialType> materialTypes = this.baseMapper.selectList(queryWrapper);
        if (materialTypes.size() > 1) {
            throw new ServiceException("料型名称对应多个颜色编号，请同时指定颜色编号");
        }
        return materialTypes.isEmpty() ? null : materialTypes.get(0);
    }
}
