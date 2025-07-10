package com.ruoyi.web.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.web.domain.*;
import com.ruoyi.web.enums.AuditTypeEnum;
import com.ruoyi.web.enums.OrderStatus;
import com.ruoyi.web.mapper.ErpPurchaseCollectionMapper;
import com.ruoyi.web.request.purchasecollection.AddPurchaseCollectionFromInfoRequest;
import com.ruoyi.web.result.DesignPatternsResult;
import com.ruoyi.web.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
public class ErpPurchaseCollectionServiceImpl implements IErpPurchaseCollectionService {
    @Autowired
    private ErpPurchaseCollectionMapper erpPurchaseCollectionMapper;

    @Autowired
    private IErpOrdersService erpOrdersService;

    @Autowired
    private IErpAuditRecordService auditRecordService;

    @Autowired
    private IErpAuditSwitchService auditSwitchService;

    @Autowired
    private IErpPurchaseInfoService erpPurchaseInfoService;

    @Autowired
    private IErpMaterialInfoService erpMaterialInfoService;

    @Autowired
    private IErpDesignPatternsService erpDesignPatternsService;

    @Override
    @Transactional
    public int deleteErpPurchaseCollectionById(Long id) {
        erpPurchaseCollectionMapper.clearErpPurchaseCollectionMaterials(id);
        return erpPurchaseCollectionMapper.deleteErpPurchaseCollectionById(id);
    }

    @Override
    @Transactional
    public int deleteErpPurchaseCollectionByIds(Long[] ids) {
        for (Long id : ids) {
            // 在删除采购单之前，处理相关的待审核记录
            auditRecordService.handleAuditableEntityDeleted(
                AuditTypeEnum.PURCHASE_AUDIT.getCode(),
                id
            );
            erpPurchaseCollectionMapper.clearErpPurchaseCollectionMaterials(id);
        }
        return erpPurchaseCollectionMapper.deleteErpPurchaseCollectionByIds(ids);
    }

    @Override
    @Transactional
    public int updateErpPurchaseCollection(ErpPurchaseCollection erpPurchaseCollection) throws IOException {
        // 获取原始记录以检查状态变更
        ErpPurchaseCollection oldCollection = selectErpPurchaseCollectionById(erpPurchaseCollection.getId());
        
        erpPurchaseCollection.setUpdateTime(DateUtils.getNowDate());
        erpPurchaseCollection.setOperator(
                SecurityUtils.getLoginUser().getUsername()
        );
        if (erpPurchaseCollection.getPicture() != null) {
            erpPurchaseCollection.setPictureUrl(
                    FileUploadUtils.upload(erpPurchaseCollection.getPicture())
            );
        }
        if (0 >= erpPurchaseCollectionMapper.updateErpPurchaseCollection(erpPurchaseCollection)) {
            throw new ServiceException("更新失败");
        }
        if (erpPurchaseCollection.getMaterialIds() != null) {
            erpPurchaseCollectionMapper.clearErpPurchaseCollectionMaterials(erpPurchaseCollection.getId());
            erpPurchaseCollectionMapper.insertErpPurchaseCollectionMaterials(
                    erpPurchaseCollection.getId(),
                    erpPurchaseCollection.getMaterialIds()
            );
        }

        // 检查状态变更
        if (erpPurchaseCollection.getStatus() != null && 
            oldCollection != null && 
            !erpPurchaseCollection.getStatus().equals(oldCollection.getStatus()) &&
            auditSwitchService.isAuditEnabled(AuditTypeEnum.PURCHASE_AUDIT.getCode())) {
            
            auditRecordService.handlePurchaseCollectionStatusChange(erpPurchaseCollection, erpPurchaseCollection.getStatus().intValue());
        }

        return 1;
    }

    @Override
    @Transactional
    public int insertFromInfo(AddPurchaseCollectionFromInfoRequest request) throws IOException {
        ErpPurchaseCollection erpPurchaseCollection = new ErpPurchaseCollection();

        List<ErpDesignPatterns> designPatternsList = erpDesignPatternsService
                .selectErpDesignPatternsListByIds(new Long[]{request.getDesignPatternId()});
        if (designPatternsList == null || designPatternsList.isEmpty()) {
            throw new ServiceException("设计总表不存在");
        }

        ErpDesignPatterns designPatterns = designPatternsList.get(0);
        ErpOrders erpOrders = erpOrdersService.selectErpOrdersById(designPatterns.getOrderId());
        if (erpOrders == null) {
            throw new ServiceException("设计总表对应订单不存在");
        }
        erpPurchaseCollection.setOrderCode(erpOrders.getInnerId());
        erpPurchaseCollection.setProductId(erpOrders.getProductId());

        ErpPurchaseInfo erpPurchaseInfo = erpPurchaseInfoService.getById(request.getPurchaseInfoId());
        if (erpPurchaseInfo == null) {
            throw new ServiceException("外购资料不存在");
        }
        ErpMaterialInfo erpMaterialInfo = erpMaterialInfoService
                .selectErpMaterialInfoByMaterialType(erpPurchaseInfo.getMaterialType());
        if (erpMaterialInfo.getSingleWeight() == null) {
            throw new ServiceException("请先完善对应物料资料的单重信息");
        }

        erpPurchaseCollection.setPictureUrl(erpPurchaseInfo.getPictureUrl());
        erpPurchaseCollection.setPurchaseCode(erpPurchaseInfo.getPurchaseCode());
        erpPurchaseCollection.setMouldNumber(erpMaterialInfo.getMouldNumber());
        erpPurchaseCollection.setSpecification(erpMaterialInfo.getSpecificationName());
        erpPurchaseCollection.setPurchaseQuantity(request.getPurchaseQuantity());
        erpPurchaseCollection.setColorCode(request.getColorCode());
        erpPurchaseCollection.setMaterialType(erpMaterialInfo.getMaterialType());
        erpPurchaseCollection.setSingleWeight(erpMaterialInfo.getSingleWeight());
        erpPurchaseCollection.setPurchaseWeight(
                erpMaterialInfo.getSingleWeight() * request.getPurchaseQuantity()
        );
        erpPurchaseCollection.setDeliveryTime(request.getDeliveryTime());
        erpPurchaseCollection.setSupplier(erpPurchaseInfo.getSupplier());
        erpPurchaseCollection.setRemarks(request.getRemarks());

        return insertErpPurchaseCollection(erpPurchaseCollection);
    }

    @Override
    @Transactional
    public int insertErpPurchaseCollection(ErpPurchaseCollection erpPurchaseCollection) throws IOException {
        // 设置初始状态为待审核
        erpPurchaseCollection.setStatus(0L);

        erpPurchaseCollection.setCreateTime(DateUtils.getNowDate());
        erpPurchaseCollection.setUpdateTime(DateUtils.getNowDate());
        erpPurchaseCollection.setCreationTime(DateUtils.getNowDate());
        erpPurchaseCollection.setOperator(
                SecurityUtils.getLoginUser().getUsername()
        );
        if (erpPurchaseCollection.getPicture() != null) {
            erpPurchaseCollection.setPictureUrl(
                    FileUploadUtils.upload(erpPurchaseCollection.getPicture())
            );
        }
        if (0 >= erpPurchaseCollectionMapper.insertErpPurchaseCollection(erpPurchaseCollection)) {
            throw new ServiceException("添加失败");
        }
        // TODO: 将订单状态修改逻辑移到审核流程中
        erpOrdersService.updateOrderStatusAutomatic(
                erpPurchaseCollection.getOrderCode(),
                (oldStatus) -> {
                    if (oldStatus == OrderStatus.IN_PRODUCTION) {
                        return OrderStatus.PURCHASING_IN_PRODUCTION;
                    } else {
                        return OrderStatus.PURCHASING;
                    }
                }
        );
        if (erpPurchaseCollection.getMaterialIds() != null) {
            erpPurchaseCollectionMapper.insertErpPurchaseCollectionMaterials(
                    erpPurchaseCollection.getId(),
                    erpPurchaseCollection.getMaterialIds()
            );
        }

        // 检查是否启用采购审核
        if (auditSwitchService.isAuditEnabled(AuditTypeEnum.PURCHASE_AUDIT.getCode())) {
            // 创建审核记录并发送通知
            auditRecordService.handlePurchaseCollectionCreated(erpPurchaseCollection);
        } else {
            // 如果未启用审核，直接设置为已审核状态
            erpPurchaseCollection.setStatus(1L);
            updateErpPurchaseCollection(erpPurchaseCollection);
        }

        return 1;
    }

    @Override
    @Transactional
    public int insertErpPurchaseCollections(List<ErpPurchaseCollection> erpPurchaseCollectionList) throws IOException {
        int count = 0;
        for (ErpPurchaseCollection erpPurchaseCollection : erpPurchaseCollectionList) {
            count += insertErpPurchaseCollection(erpPurchaseCollection);
        }
        return count;
    }

    private ErpPurchaseCollection fillMaterialIds(ErpPurchaseCollection erpPurchaseCollection) {
        erpPurchaseCollection.setMaterialIds(
                erpPurchaseCollectionMapper
                        .getErpPurchaseCollectionMaterialIds(erpPurchaseCollection.getId())
        );
        return erpPurchaseCollection;
    }

    private List<ErpPurchaseCollection> fillMaterialIds(List<ErpPurchaseCollection> erpPurchaseCollectionList) {
        for (ErpPurchaseCollection erpPurchaseCollection : erpPurchaseCollectionList) {
            fillMaterialIds(erpPurchaseCollection);
        }
        return erpPurchaseCollectionList;
    }

    @Override
    public ErpPurchaseCollection selectErpPurchaseCollectionById(Long id) {
        return fillMaterialIds(erpPurchaseCollectionMapper.selectErpPurchaseCollectionById(id));
    }

    @Override
    public List<ErpPurchaseCollection> selectErpPurchaseCollectionList(ErpPurchaseCollection erpPurchaseCollection) {
        return fillMaterialIds(erpPurchaseCollectionMapper.selectErpPurchaseCollectionList(erpPurchaseCollection));
    }

    @Override
    public List<ErpPurchaseCollection> selectErpPurchaseCollectionListByIds(Long[] ids) {
        return fillMaterialIds(erpPurchaseCollectionMapper.selectErpPurchaseCollectionListByIds(ids));
    }

    @Override
    public void applyApprovedStatus(ErpPurchaseCollection erpPurchaseCollection) {
        // 直接更新数据库，不包含其他业务逻辑
        erpPurchaseCollectionMapper.updateErpPurchaseCollection(erpPurchaseCollection);
    }
}
