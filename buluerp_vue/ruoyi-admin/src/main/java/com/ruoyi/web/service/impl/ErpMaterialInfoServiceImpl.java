package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.file.InvalidExtensionException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.web.domain.*;
import com.ruoyi.web.mapper.ErpMaterialInfoMapper;
import com.ruoyi.web.request.material.AddMaterialInfoRequest;
import com.ruoyi.web.request.material.AddPurchasedMaterialRequest;
import com.ruoyi.web.request.mould.ListMouldRequest;
import com.ruoyi.web.request.product.UpdateProductRequest;
import com.ruoyi.web.service.*;
import com.ruoyi.web.util.ModelConversionUtils;
import com.ruoyi.framework.config.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.rmi.ServerException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ErpMaterialInfoServiceImpl implements IErpMaterialInfoService {
    private static final Logger log = LoggerFactory.getLogger(ErpMaterialInfoServiceImpl.class);

    @Autowired
    private ErpMaterialInfoMapper erpMaterialInfoMapper;

    @Autowired
    private IErpPurchaseInfoService erpPurchaseInfoService;

    @Autowired
    private IErpDesignStyleService erpDesignStyleService;

    @Autowired
    private IErpProductionScheduleService erpProductionScheduleService;

    @Autowired
    private IErpPackagingDetailService erpPackagingDetailService;

    @Autowired
    private IErpMaterialTypeService erpMaterialTypeService;

    @Autowired
    private IErpProductsService erpProductsService;

    @Autowired
    private IErpMouldService erpMouldService;

    @Autowired
    private ModelConversionUtils modelConversionUtils;

    @Autowired
    private ServerConfig serverConfig;

    private ErpMaterialInfo fill(ErpMaterialInfo erpMaterialInfo) {
        LambdaQueryWrapper<ErpPurchaseInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ErpPurchaseInfo::getMaterialId, erpMaterialInfo.getId());
        ErpPurchaseInfo erpPurchaseInfos =
                erpPurchaseInfoService.getOne(queryWrapper);
        erpMaterialInfo.setPurchaseInfo(erpPurchaseInfos);
        return erpMaterialInfo;
    }

    private List<ErpMaterialInfo> fill(List<ErpMaterialInfo> erpMaterialInfoList) {
        for (ErpMaterialInfo erpMaterialInfo : erpMaterialInfoList) {
            fill(erpMaterialInfo);
        }
        return erpMaterialInfoList;
    }

    private void checkUnique(ErpMaterialInfo erpMaterialInfo) {
        if (erpMaterialInfo.getMouldNumber() != null) {
            ErpMaterialInfo original = erpMaterialInfoMapper
                    .selectByMouldNumber(erpMaterialInfo.getMouldNumber());
            if (original != null && !Objects.equals(original.getId(), erpMaterialInfo.getId())) {
                throw new ServiceException("模具编号重复");
            }
        }
    }

    private void checkReferences(ErpMaterialInfo erpMaterialInfo) {
        if (erpMaterialInfo.getMaterialType() != null) {
            ErpMaterialType type = erpMaterialTypeService.getByName(erpMaterialInfo.getMaterialType());
            if (type == null) {
                throw new ServiceException("对应物料类型不存在");
            }
        }
        if (erpMaterialInfo.getMouldNumber() != null) {
            ListMouldRequest request = new ListMouldRequest();
            request.setMouldNumber(erpMaterialInfo.getMouldNumber());
            if (CollectionUtils.isEmpty(erpMouldService.list(request))) {
                throw new ServiceException("对应模具不存在");
            }
        }
    }

    private void check(ErpMaterialInfo erpMaterialInfo) {
        checkUnique(erpMaterialInfo);
        checkReferences(erpMaterialInfo);
    }

    @Override
    public List<ErpMaterialInfo> selectErpMaterialInfoList(ErpMaterialInfo erpMaterialInfo) {
        return fill(erpMaterialInfoMapper.selectErpMaterialInfoList(erpMaterialInfo));
    }

    @Override
    public List<ErpMaterialInfo> selectErpMaterialInfoListByIds(Long[] ids) {
        return fill(erpMaterialInfoMapper.selectErpMaterialInfoListByIds(ids));
    }

    @Override
    public List<ErpMaterialInfo> listByMaterialType(String materialType) {
        return fill(erpMaterialInfoMapper.selectByMaterialType(materialType));
    }

    @Override
    public ErpMaterialInfo selectErpMaterialInfoById(Long id) {
        return erpMaterialInfoMapper.selectErpMaterialInfoById(id);
    }

    @Override
    public ErpMaterialInfo getByMouldNumber(String mouldNumber) {
        return erpMaterialInfoMapper.selectByMouldNumber(mouldNumber);
    }

    @Override
    @Transactional
    public Long insertErpMaterialInfo(AddMaterialInfoRequest request) throws IOException {
        request.setCreatTime(DateUtils.getNowDate());
        request.setUpdateTime(DateUtils.getNowDate());
        check(request);
        // if (erpMaterialInfoMapper.selectErpMaterialInfoByMaterialType(request.getMaterialType()) != null) {
        //     throw new ServiceException("物料类型已存在");
        // }
        if (request.getDrawingReferenceFile() != null) {
            String url = FileUploadUtils.upload(request.getDrawingReferenceFile());
            request.setDrawingReference(url);
        }

        // 处理3D模型文件上传与转换
        if (request.getModelFile() != null && !request.getModelFile().isEmpty()) {
            String modelSubDir = modelConversionUtils.getModel3dPath();
            String stpUrlPath;
            try {
                stpUrlPath = FileUploadUtils.upload(
                        RuoYiConfig.getUploadPath() + modelSubDir,
                        request.getModelFile(),
                        MimeTypeUtils.MODEL_3D_EXTENSION
                );
            } catch (InvalidExtensionException e) {
                throw new ServiceException("3D模型文件格式不支持，仅支持STP/STEP格式");
            }

            // 根据URL路径还原文件绝对路径
            // stpUrlPath = "/profile/upload/model/3d/2025/04/28/filename_xxx.stp"
            String stpAbsPath = RuoYiConfig.getProfile()
                    + StringUtils.substringAfter(stpUrlPath, Constants.RESOURCE_PREFIX);
            File stpFile = new File(stpAbsPath);

            if (stpFile.exists()) {
                String gltfFileName = stpFile.getName().replaceAll("\\.(stp|step)$", ".gltf");
                String gltfAbsPath = stpFile.getParent() + File.separator + gltfFileName;

                boolean converted = modelConversionUtils.convertStpToGltf(
                        stpFile.getAbsolutePath(), gltfAbsPath
                );

                if (converted) {
                    String gltfUrlPath = StringUtils.substringBeforeLast(stpUrlPath, "/")
                            + "/" + gltfFileName;
                    String gltfUrl = serverConfig.getUrl() + gltfUrlPath;
                    request.setModelUrl(gltfUrl);
                } else {
                    log.warn("3D模型转换失败，仅保存原始STP文件: {}", stpFile.getName());
                }
            } else {
                log.warn("STP文件未找到，跳过转换: {}", stpAbsPath);
            }
        }

        if (0 == erpMaterialInfoMapper.insertErpMaterialInfo(request)) {
            throw new ServerException("物料新增失败");
        }
        if (StringUtils.isNotBlank(request.getProductCode())) {
            Long productId = erpProductsService.getIdByInnerId(request.getProductCode());
            if (productId == null) {
                throw new ServiceException("产品不存在");
            }
            List<ErpProducts> products = erpProductsService.selectErpProductsListByIds(new Long[]{productId});
            if (products.isEmpty()) {
                throw new ServiceException("产品不存在");
            }
            ErpProducts product = products.get(0);
            product.getMaterialIds().add(request.getId().intValue());
            UpdateProductRequest updateProductRequest = new UpdateProductRequest();
            updateProductRequest.setId(product.getId());
            updateProductRequest.setMaterialIds(product.getMaterialIds());
            erpProductsService.updateErpProducts(updateProductRequest);
        }
        return request.getId();
    }

    @Override
    @Transactional
    public int insertErpMaterialInfos(List<ErpMaterialInfo> erpMaterialInfos) {
        int count = 0;
        for (ErpMaterialInfo erpMaterialInfo : erpMaterialInfos) {
            erpMaterialInfo.setCreatTime(DateUtils.getNowDate());
            erpMaterialInfo.setUpdateTime(DateUtils.getNowDate());
            count += erpMaterialInfoMapper.insertErpMaterialInfo(erpMaterialInfo);
        }
        return count;
    }

    @Override
    @Transactional
    public Long insertPurchased(AddPurchasedMaterialRequest request) throws IOException {
        AddMaterialInfoRequest materialRequest = new AddMaterialInfoRequest();
        materialRequest.setMouldNumber(request.getMouldNumber());
        materialRequest.setSpecificationName(request.getSpecificationName());
        materialRequest.setMaterialType(request.getMaterialType());
        materialRequest.setSingleWeight(request.getSingleWeight());
        materialRequest.setProductCode(request.getProductCode());
        materialRequest.setModelFile(request.getModelFile());

        Long materialId = insertErpMaterialInfo(materialRequest);
        if (materialId == null) {
            throw new ServiceException("物料新增失败");
        }

        ErpPurchaseInfo erpPurchaseInfo = new ErpPurchaseInfo();
        erpPurchaseInfo.setPurchaseCode(request.getPurchaseCode());

        if (request.getPictureFile() != null) {
            String url = FileUploadUtils.upload(request.getPictureFile());
            erpPurchaseInfo.setPictureUrl(url);
        } else {
            erpPurchaseInfo.setPictureUrl(request.getPictureUrl());
        }

        erpPurchaseInfo.setUnitPrice(request.getUnitPrice());
        erpPurchaseInfo.setMaterialId(materialId);
        erpPurchaseInfo.setSupplier(request.getSupplier());
        if (0 == erpPurchaseInfoService.insertErpPurchaseInfo(erpPurchaseInfo)) {
            throw new ServiceException("外购信息新增失败");
        }
        return materialId;
    }

    @Override
    @Transactional
    public int updateErpMaterialInfo(ErpMaterialInfo erpMaterialInfo) throws IOException {
        erpMaterialInfo.setUpdateTime(DateUtils.getNowDate());
        erpMaterialInfo.setMouldNumber(null);
        check(erpMaterialInfo);
        if (Boolean.TRUE.equals(erpMaterialInfo.getDeleteDrawingReference())) {
            erpMaterialInfoMapper.deleteErpMaterialInfoDrawingReferenceById(erpMaterialInfo.getId());
            // 联动删除使用该物料的设计造型图片
            erpDesignStyleService.updateDesignStylePictureByMaterialId(erpMaterialInfo.getId(), null);
        } else if (erpMaterialInfo.getDrawingReferenceFile() != null) {
            String url = FileUploadUtils.upload(erpMaterialInfo.getDrawingReferenceFile());
            erpMaterialInfo.setDrawingReference(url);
            // 联动更新使用该物料的设计造型图片
            erpDesignStyleService.updateDesignStylePictureByMaterialId(erpMaterialInfo.getId(), url);
        }

        // 处理3D模型文件更新
        if (erpMaterialInfo.getModelFile() != null && !erpMaterialInfo.getModelFile().isEmpty()) {
            String modelSubDir = modelConversionUtils.getModel3dPath();
            String stpUrlPath;
            try {
                stpUrlPath = FileUploadUtils.upload(
                        RuoYiConfig.getUploadPath() + modelSubDir,
                        erpMaterialInfo.getModelFile(),
                        MimeTypeUtils.MODEL_3D_EXTENSION
                );
            } catch (InvalidExtensionException e) {
                throw new ServiceException("3D模型文件格式不支持，仅支持STP/STEP格式");
            }

            String stpAbsPath = RuoYiConfig.getProfile()
                    + StringUtils.substringAfter(stpUrlPath, Constants.RESOURCE_PREFIX);
            File stpFile = new File(stpAbsPath);

            if (stpFile.exists()) {
                String gltfFileName = stpFile.getName().replaceAll("\\.(stp|step)$", ".gltf");
                String gltfAbsPath = stpFile.getParent() + File.separator + gltfFileName;

                boolean converted = modelConversionUtils.convertStpToGltf(
                        stpFile.getAbsolutePath(), gltfAbsPath
                );

                if (converted) {
                    String gltfUrlPath = StringUtils.substringBeforeLast(stpUrlPath, "/")
                            + "/" + gltfFileName;
                    String gltfUrl = serverConfig.getUrl() + gltfUrlPath;
                    erpMaterialInfo.setModelUrl(gltfUrl);
                } else {
                    log.warn("3D模型转换失败，仅保存原始STP文件: {}", stpFile.getName());
                }
            } else {
                log.warn("STP文件未找到，跳过转换: {}", stpAbsPath);
            }
        }

        return erpMaterialInfoMapper.updateErpMaterialInfo(erpMaterialInfo);
    }

    @Override
    @Transactional
    public int deleteErpMaterialInfoByIds(Long[] ids) {
        for (Long id : ids) {
            LambdaQueryWrapper<ErpProductionSchedule> scheduleQuery = new LambdaQueryWrapper<>();
            scheduleQuery.eq(ErpProductionSchedule::getMaterialId, id);
            if (erpProductionScheduleService.count(scheduleQuery) > 0) {
                throw new ServiceException("该物料已被布产，不能删除");
            }
            LambdaQueryWrapper<ErpPackagingDetail> packagingDetailQuery = new LambdaQueryWrapper<>();
            packagingDetailQuery.eq(ErpPackagingDetail::getMaterialId, id);
            if (erpPackagingDetailService.count(packagingDetailQuery) > 0) {
                throw new ServiceException("该物料已被拉线组包，不能删除");
            }
        }
        erpPurchaseInfoService.deleteErpPurchaseInfoByMaterialIds(ids);
        return erpMaterialInfoMapper.deleteErpMaterialInfoByIds(ids);
    }

    @Override
    @Transactional
    public int deleteErpMaterialInfoById(Long id) {
        return deleteErpMaterialInfoByIds(new Long[]{id});
    }
}
