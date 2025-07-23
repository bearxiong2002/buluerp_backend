package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.web.domain.*;
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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class ErpProductionScheduleServiceImpl
        extends ServiceImpl<ErpProductionScheduleMapper, ErpProductionSchedule>
        implements IErpProductionScheduleService {

    @Autowired
    private IErpAuditRecordService auditRecordService;

    @Autowired
    private IErpAuditSwitchService auditSwitchService;

    @Autowired
    private IErpProductsService erpProductsService;

    @Autowired
    private IErpOrdersService erpOrdersService;

    @Autowired
    private IErpMaterialInfoService erpMaterialInfoService;

    @Autowired
    private IErpDesignPatternsService erpDesignPatternsService;

    @Autowired
    private IErpOrdersService erpOrderService;

    @Autowired
    private IErpPurchaseCollectionService erpPurchaseCollectionService;

    @Autowired
    private IErpProductionArrangeService erpProductionArrangeService;

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

    private void checkReferences(ErpProductionSchedule erpProductionSchedule) {
        if (erpProductionSchedule.getOrderCode()!= null) {
            ErpOrders order = erpOrdersService.selectByOrderCode(erpProductionSchedule.getOrderCode());
            if (order == null) {
                throw new ServiceException("订单不存在");
            }
        }
    }

    private void check(ErpProductionSchedule erpProductionSchedule) {
        checkUnique(erpProductionSchedule);
        checkReferences(erpProductionSchedule);
    }

    @Override
    @Transactional
    public int insertErpProductionSchedule(ErpProductionSchedule erpProductionSchedule) throws IOException {
        check(erpProductionSchedule);
        ErpOrders erpOrders = erpOrdersService.selectByOrderCode(erpProductionSchedule.getOrderCode());
        if (!Objects.equals(erpOrders.getStatus(), OrderStatus.PRODUCTION_SCHEDULE_PENDING.getValue(erpOrderService))) {
            throw new ServiceException("订单不在布产计划定制阶段");
        }

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
        // erpOrdersService.updateOrderStatusAutomatic(
        //         erpProductionSchedule.getOrderCode(),
        //         OrderStatus.PRODUCTION_SCHEDULING
        // );
        if (erpProductionSchedule.getMaterialIds() != null) {
            getBaseMapper().insertProductionScheduleMaterialIds(
                    erpProductionSchedule.getId(),
                    erpProductionSchedule.getMaterialIds()
            );
        }

        // 移除创建时的审核触发，布产审核只保留布产完成审核

        return 1;
    }

    @Override
    public boolean isAllScheduled(String orderCode) {
        ErpOrders order = erpOrderService.selectByOrderCode(orderCode);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        return order.getAllScheduled();
    }

    /**
     * 标记完成方法与具体逻辑解耦，方便审核
     * @param orderCode
     */
    @Override
    @Transactional
    public void markAllScheduled(String orderCode) {
        ErpOrders order = erpOrderService.selectByOrderCode(orderCode);
        if (!Objects.equals(order.getStatus(), OrderStatus.PRODUCTION_SCHEDULE_PENDING.getValue(erpOrderService))) {
            throw new ServiceException("订单不在布产计划定制阶段");
        }
        
        // 检查是否启用布产审核
        if (auditSwitchService.isAuditEnabled(AuditTypeEnum.PRODUCTION_AUDIT.getCode())) {
            // 发起布产完成审核
            auditRecordService.handleProductionScheduleCompleteAudit(orderCode);
        } else {
            // 如果未启用审核，直接完成
            executeMarkAllScheduled(orderCode);
        }
    }
    
    /**
     * 执行标记全部布产完成的具体逻辑
     */
    @Override
    public void executeMarkAllScheduled(String orderCode) {
        ErpOrders order = erpOrderService.selectByOrderCode(orderCode);
        erpOrdersService.updateOrderAllScheduled(order.getId(), true);
        if (isAllProduced(orderCode)) {
            if (erpPurchaseCollectionService.isAllPurchaseCompleted(orderCode)) {
                erpOrdersService.updateOrderStatusAutomatic(orderCode, OrderStatus.MATERIAL_IN_INVENTORY);
            } else {
                erpOrdersService.updateOrderStatusAutomatic(orderCode, OrderStatus.PRODUCTION_DONE_PURCHASING);
            }
        } else if (isAllProducing(orderCode)) {
            erpOrdersService.updateOrderStatusAutomatic(orderCode, OrderStatus.IN_PRODUCTION);
        } else {
            erpOrdersService.updateOrderStatusAutomatic(orderCode, OrderStatus.PRODUCTION_PENDING);
        }
    }

    @Override
    public boolean isProduced(Long scheduleId) {
        ErpProductionSchedule schedule = getBaseMapper().selectById(scheduleId);
        if (schedule == null) {
            throw new ServiceException("布产计划不存在");
        }
        ErpProductionArrange arrange = erpProductionArrangeService.getById(schedule.getArrangeId());
        if (arrange == null) {
            return false;
        }
        return arrange.getCompletionTime() != null && !arrange.getCompletionTime().after(DateUtils.getLastSecondOfToday());
    }

    @Override
    public boolean isProducing(Long scheduleId) {
        ErpProductionSchedule schedule = getBaseMapper().selectById(scheduleId);
        return schedule.getArrangeId() != null;
    }

    @Override
    public boolean isCurrentlyProduced(String orderCode) {
        LambdaQueryWrapper<ErpProductionSchedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ErpProductionSchedule::getOrderCode, orderCode);
        List<ErpProductionSchedule> list = getBaseMapper().selectList(queryWrapper);
        for (ErpProductionSchedule erpProductionSchedule : list) {
            if (!isProduced(erpProductionSchedule.getId())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isCurrentlyProducing(String orderCode) {
        LambdaQueryWrapper<ErpProductionSchedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ErpProductionSchedule::getOrderCode, orderCode);
        List<ErpProductionSchedule> list = getBaseMapper().selectList(queryWrapper);
        for (ErpProductionSchedule erpProductionSchedule : list) {
            if (!isProducing(erpProductionSchedule.getId())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isAllProduced(String orderCode) {
        return isAllScheduled(orderCode) && isCurrentlyProduced(orderCode);
    }

    @Override
    public boolean isAllProducing(String orderCode) {
        return isAllScheduled(orderCode) && isCurrentlyProducing(orderCode);
    }

    @Override
    @Transactional
    public int insertFromMaterial(AddProductionScheduleFromMaterialRequest request) throws IOException {
        ErpProductionSchedule schedule = new ErpProductionSchedule();

        if (request.getDesignPatternId() != null) {
            Long designPatternId = request.getDesignPatternId();
            List<ErpDesignPatterns> designPatterns = erpDesignPatternsService
                    .selectErpDesignPatternsListByIds(new Long[]{designPatternId});
            if (designPatterns.isEmpty()) {
                throw new ServiceException("设计总表不存在");
            }
            ErpDesignPatterns designPattern = designPatterns.get(0);
            schedule.setProductId(designPattern.getProductId());
            ErpOrders order = erpOrdersService.selectByOrderCode(designPattern.getOrderId());
            if (order == null) {
                throw new ServiceException("设计总表项对应订单不存在或已被删除");
            }
            schedule.setOrderCode(order.getInnerId());
            schedule.setCustomerId(order.getCustomerId());
        } else if (request.getOrderCode() != null) {
            ErpOrders order = erpOrdersService.selectByOrderCode(request.getOrderCode());
            if (order == null) {
                throw new ServiceException("订单不存在");
            }
            schedule.setOrderCode(order.getInnerId());
            schedule.setProductId(order.getProductId());
            schedule.setCustomerId(order.getCustomerId());
        } else {
            throw new ServiceException("未指定订单或设计总表");
        }

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
            !erpProductionSchedule.getStatus().equals(oldSchedule.getStatus()) ) {
            throw new ServiceException("不允许直接修改布产状态");
        }

        return 1;
    }

    @Override
    public int attachToArrange(Long productionArrangeId, List<Long> productionScheduleIds) {
        int result = baseMapper.attatchToArrange(productionArrangeId, productionScheduleIds);

        Set<String> orderCodes = new HashSet<>();
        for  (Long productionScheduleId : productionScheduleIds) {
            ErpProductionSchedule schedule = getById(productionScheduleId);
            if (schedule != null) {
                orderCodes.add(schedule.getOrderCode());
            }
        }

        for (String orderCode : orderCodes) {
            if (isAllProduced(orderCode)) {
                erpOrderService.updateOrderStatusAutomatic(orderCode, OrderStatus.MATERIAL_IN_INVENTORY);
            } else if (isAllProducing(orderCode)) {
                erpOrderService.updateOrderStatusAutomatic(orderCode, OrderStatus.IN_PRODUCTION);
            }
        }

        return result;
    }

    @Override
    @Transactional
    public void removeChecked(Long id) {
        ErpProductionSchedule schedule = getById(id);
        if (schedule == null) {
            throw new ServiceException("布产" + id + "不存在");
        }
        ErpOrders order = erpOrdersService.selectByOrderCode(schedule.getOrderCode());
        if (order != null && order.getAllScheduled()) {
            throw new ServiceException("订单已完成布产计划定制，不允许删除布产" + id);
        }
        if (!removeById(id)) {
            throw new ServiceException("删除布产" + id + "失败");
        }
    }

    @Override
    @Transactional
    public void removeBatchChecked(List<Long> ids) {
        for (Long id : ids) {
            removeChecked(id);
        }
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
