package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.web.domain.ErpDesignPatterns;
import com.ruoyi.web.domain.ErpMaterialInfo;
import com.ruoyi.web.domain.ErpOrders;
import com.ruoyi.web.domain.ErpProductionSchedule;
import com.ruoyi.web.enums.AuditTypeEnum;
import com.ruoyi.web.enums.OrderStatus;
import com.ruoyi.web.mapper.ErpProductionScheduleMapper;
import com.ruoyi.web.request.productionschedule.AddProductionScheduleFromMaterialRequest;
import com.ruoyi.web.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class ErpProductionScheduleServiceImpl
        extends ServiceImpl<ErpProductionScheduleMapper, ErpProductionSchedule>
        implements IErpProductionScheduleService {

    @Autowired
    private IErpAuditRecordService auditRecordService;

    @Autowired
    private IErpAuditSwitchService auditSwitchService;

    @Autowired
    private IErpOrdersService erpOrdersService;

    @Autowired
    private IErpMaterialInfoService erpMaterialInfoService;

    @Autowired
    private IErpDesignPatternsService erpDesignPatternsService;

    @Autowired
    private IErpProductsService erpProductsService;

    @Autowired
    private IErpOrdersService erpOrderService;

    private void checkUnique(ErpProductionSchedule erpProductionSchedule) {
        // if (erpProductionSchedule.getOrderCode() != null) {
        //     LambdaQueryWrapper<ErpProductionSchedule> queryWrapper = new LambdaQueryWrapper<>();
        //     queryWrapper.eq(ErpProductionSchedule::getOrderCode, erpProductionSchedule.getOrderCode());
        //     ErpProductionSchedule original = this.getOne(queryWrapper);
        //     if (original != null && !Objects.equals(original.getId(), erpProductionSchedule.getId())) {
        //         throw new ServiceException("此订单已存在布产" + erpProductionSchedule.getId());
        //     }
        // }
    }

    private void check(ErpProductionSchedule erpProductionSchedule) {
        checkUnique(erpProductionSchedule);
    }

    @Override
    @Transactional
    public int insertErpProductionSchedule(ErpProductionSchedule erpProductionSchedule) throws IOException {
        check(erpProductionSchedule);

        // 设置初始状态为待审核
        erpProductionSchedule.setStatus(0L);
        
        if (erpProductionSchedule.getPicture() != null) {
            String url = FileUploadUtils.upload(erpProductionSchedule.getPicture());
            erpProductionSchedule.setPictureUrl(url);
        } else {
            erpProductionSchedule.setPictureUrl(null);
        }
        erpProductionSchedule.setOperator(SecurityUtils.getUsername());
        erpProductionSchedule.setCreationTime(DateUtils.getNowDate());
        if (!CollectionUtils.isEmpty(erpProductionSchedule.getMaterialIds())) {
            ErpMaterialInfo materialInfo = erpMaterialInfoService.selectErpMaterialInfoById(
                    erpProductionSchedule.getMaterialIds().get(0)
            );
            erpProductionSchedule.setMouldNumber(materialInfo.getMouldNumber());
            erpProductionSchedule.setMaterialType(materialInfo.getMaterialType());
        }
        if (0 == getBaseMapper().insert(erpProductionSchedule)) {
            throw new ServiceException("操作失败");
        }
        ErpOrders order = erpOrdersService.selectByOrderCode(erpProductionSchedule.getOrderCode());
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        erpProductionSchedule.setProductId(order.getProductId());
        // TODO: 将订单状态修改逻辑移到审核流程中
        erpOrdersService.updateOrderStatusAutomatic(
                erpProductionSchedule.getOrderCode(),
                (oldStatus) -> {
                    if (oldStatus == OrderStatus.PRODUCTION_SCHEDULING) {
                        return OrderStatus.PRODUCTION_DONE_PURCHASING;
                    } else {
                        return OrderStatus.PRODUCTION_SCHEDULING;
                    }
                }
        );
        if (erpProductionSchedule.getMaterialIds() != null) {
            getBaseMapper().insertProductionScheduleMaterialIds(
                    erpProductionSchedule.getId(),
                    erpProductionSchedule.getMaterialIds()
            );
        }

        // 检查是否启用布产审核
        if (auditSwitchService.isAuditEnabled(AuditTypeEnum.PRODUCTION_AUDIT.getCode())) {
            // 创建审核记录并发送通知
            auditRecordService.handleProductionScheduleCreated(erpProductionSchedule);
        } else {
            // 如果未启用审核，直接设置为已审核状态
            erpProductionSchedule.setStatus(1L);
            updateById(erpProductionSchedule);
        }

        return 1;
    }

    @Override
    @Transactional
    public int insertFromMaterial(AddProductionScheduleFromMaterialRequest request) throws IOException {
        ErpProductionSchedule schedule = new ErpProductionSchedule();
        Long designPatternId = request.getDesignPatternId();
        List<ErpDesignPatterns> designPatterns = erpDesignPatternsService
                .selectErpDesignPatternsListByIds(new Long[]{designPatternId});
        if (designPatterns.isEmpty()) {
            throw new ServiceException("设计总表不存在");
        }
        ErpDesignPatterns designPattern = designPatterns.get(0);
        schedule.setProductId(erpProductsService.getIdByInnerId(designPattern.getProductId()) );

        ErpOrders order = erpOrdersService.selectErpOrdersById(designPattern.getOrderId());
        if (order == null) {
            throw new ServiceException("设计总表项对应订单不存在或已被删除");
        }
        schedule.setOrderCode(order.getInnerId());

        schedule.setProductionTime(request.getProductionTime());

        ErpMaterialInfo materialInfo = erpMaterialInfoService.selectErpMaterialInfoById(
                request.getMaterialId()
        );
        if (materialInfo == null) {
            throw new ServiceException("物料信息不存在");
        }
        schedule.setMouldNumber(materialInfo.getMouldNumber());
        schedule.setMouldCondition(materialInfo.getMouldStatus());
        schedule.setPictureUrl(materialInfo.getDrawingReference());

        schedule.setColorCode(request.getColorCode());
        schedule.setUsage(request.getUsage());

        schedule.setMaterialType(materialInfo.getMaterialType());
        schedule.setCavityCount(materialInfo.getCavityCount());
        schedule.setSingleWeight(materialInfo.getSingleWeight());

        schedule.setProductionQuantity(request.getProductionQuantity().intValue());
        schedule.setProductionMouldCount(request.getProductionMouldCount().intValue());
        schedule.setProductionWeight(request.getProductionWeight());
        schedule.setColorPowderNeeded(request.getColorPowderNeeded().intValue());
        schedule.setCycleTime(request.getCycleTime());
        schedule.setTimeHours(request.getTimeHours());
        schedule.setShipmentTime(request.getShipmentTime());
        schedule.setSupplier(request.getSupplier());
        schedule.setMouldManufacturer(request.getMouldManufacturer());

        schedule.setCustomerId(order.getCustomerId());
        schedule.setMaterialId(materialInfo.getId());

        return insertErpProductionSchedule(schedule);
    }

    @Override
    @Transactional
    public int updateErpProductionSchedule(ErpProductionSchedule erpProductionSchedule) throws IOException {
        check(erpProductionSchedule);

        // 获取原始记录以检查状态变更
        ErpProductionSchedule oldSchedule = getById(erpProductionSchedule.getId());
        
        if (erpProductionSchedule.getPicture() != null) {
            String url = FileUploadUtils.upload(erpProductionSchedule.getPicture());
            erpProductionSchedule.setPictureUrl(url);
        } else {
            erpProductionSchedule.setPictureUrl(null);
        }
        erpProductionSchedule.setOperator(SecurityUtils.getUsername());
        if (0 == getBaseMapper().updateById(erpProductionSchedule)) {
            throw new ServiceException("操作失败");
        }
        if (erpProductionSchedule.getMaterialIds() != null) {
            getBaseMapper().clearProductionScheduleMaterialIds(erpProductionSchedule.getId());
            getBaseMapper().insertProductionScheduleMaterialIds(
                    erpProductionSchedule.getId(),
                    erpProductionSchedule.getMaterialIds()
            );
        }

        // 检查状态变更
        if (erpProductionSchedule.getStatus() != null &&
            oldSchedule != null &&
            !erpProductionSchedule.getStatus().equals(oldSchedule.getStatus()) &&
            auditSwitchService.isAuditEnabled(AuditTypeEnum.PRODUCTION_AUDIT.getCode())) {
            
            auditRecordService.handleProductionScheduleStatusChange(oldSchedule, erpProductionSchedule.getStatus().intValue());
        }

        return 1;
    }

    @Override
    public int attatchToArrange(Long productionArrangeId, List<Long> productionScheduleIds) {
        return baseMapper.attatchToArrange(productionArrangeId, productionScheduleIds);
    }

    @Override
    public List<Long> getProductionScheduleMaterialIds(Long productionScheduleId) {
        return baseMapper.listProductionScheduleMaterialIds(productionScheduleId);
    }

    @Override
    @Transactional
    public int removeErpProductionScheduleList(List<Long> ids) {
        for (Long id : ids) {
            // 在删除布产计划之前，处理相关的待审核记录
            auditRecordService.handleAuditableEntityDeleted(
                AuditTypeEnum.PRODUCTION_AUDIT.getCode(),
                id
            );
            getBaseMapper().clearProductionScheduleMaterialIds(id);
        }
        return getBaseMapper().deleteBatchIds(ids);
    }

    @Override
    public void applyApprovedStatus(ErpProductionSchedule erpProductionSchedule) {
        // 直接更新数据库，不包含其他业务逻辑
        updateById(erpProductionSchedule);
    }
}
