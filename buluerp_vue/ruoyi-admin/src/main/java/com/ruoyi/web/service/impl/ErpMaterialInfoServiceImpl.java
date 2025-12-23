package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.web.domain.*;
import com.ruoyi.web.mapper.ErpMaterialInfoMapper;
import com.ruoyi.web.request.material.AddMaterialInfoRequest;
import com.ruoyi.web.request.material.AddPurchasedMaterialRequest;
import com.ruoyi.web.request.mould.ListMouldRequest;
import com.ruoyi.web.request.product.UpdateProductRequest;
import com.ruoyi.web.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ErpMaterialInfoServiceImpl implements IErpMaterialInfoService {
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
        if (0 == erpMaterialInfoMapper.insertErpMaterialInfo(request)) {
            throw new ServerException("物料新增失败");
        }
        if (request.getProductCode() != null) {
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
