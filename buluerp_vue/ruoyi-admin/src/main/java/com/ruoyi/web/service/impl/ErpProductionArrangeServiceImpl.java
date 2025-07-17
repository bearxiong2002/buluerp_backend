package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.reflect.ReflectUtils;
import com.ruoyi.web.domain.ErpProductionArrange;
import com.ruoyi.web.domain.ErpProductionSchedule;
import com.ruoyi.web.enums.OrderStatus;
import com.ruoyi.web.exception.AutoLogException;
import com.ruoyi.web.mapper.ErpProductionArrangeMapper;
import com.ruoyi.web.mapper.ErpProductionScheduleMapper;
import com.ruoyi.web.request.arrange.AddProductionArrangeFromScheduleRequest;
import com.ruoyi.web.service.IErpOrdersService;
import com.ruoyi.web.service.IErpProductionArrangeService;
import com.ruoyi.web.service.IErpProductionScheduleService;
import com.ruoyi.web.service.IErpPurchaseCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.web.annotation.MarkNotificationsAsRead;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

@Service
public class ErpProductionArrangeServiceImpl
        extends ServiceImpl<ErpProductionArrangeMapper, ErpProductionArrange>
        implements IErpProductionArrangeService {
    @Autowired
    private IErpProductionScheduleService erpProductionScheduleService;

    @Autowired
    private IErpOrdersService erpOrdersService;

    @Autowired
    private IErpPurchaseCollectionService erpPurchaseCollectionService;

    @Override
    @Transactional
    public int insertErpProductionArrangeList(List<ErpProductionArrange> erpProductionArranges) throws IOException {
        int count = 0;
        for (ErpProductionArrange erpProductionArrange : erpProductionArranges) {
            erpProductionArrange.setCreationTime(DateUtils.getNowDate());
            erpProductionArrange.setOperator(SecurityUtils.getUsername());
            Date completionTime = erpProductionArrange.getCompletionTime();
            if (completionTime != null) {
                erpProductionArrange.setCompletionTime(null);
            }
            if (erpProductionArrange.getPicture() != null) {
                String url = FileUploadUtils.upload(erpProductionArrange.getPicture());
                erpProductionArrange.setPictureUrl(url);
            }
            count += baseMapper.insert(erpProductionArrange);
            if (completionTime != null) {
                markArrangeComplete(erpProductionArrange.getId(), completionTime);
            }
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @MarkNotificationsAsRead(businessType = "PRODUCTION_SCHEDULE", businessIdsExpression = "#request.scheduleIds")
    public int insertFromSchedule(AddProductionArrangeFromScheduleRequest request) throws IOException {
        List<ErpProductionSchedule> schedules = erpProductionScheduleService
                .listByIds(request.getScheduleIds());
        if (schedules.size() != request.getScheduleIds().size()) {
            throw new ServiceException("部分布产ID无效");
        }
        if (schedules.stream().anyMatch(schedule -> schedule.getArrangeId() != null)) {
            throw new ServiceException("部分布产已排产");
        }
        ErpProductionArrange erpProductionArrange = new ErpProductionArrange();
        erpProductionArrange.setPicture(request.getPictureFile());
        erpProductionArrange.setPictureUrl(request.getPictureUrl());
        erpProductionArrange.setMouldOutput(request.getMouldOutput());
        erpProductionArrange.setScheduledTime(request.getScheduledTime());
        erpProductionArrange.setRemarks(request.getRemarks());

        ErpProductionSchedule first = schedules.get(0);
        String diffField = schedules.stream()
                .skip(1)
                .map(schedule -> {
                    if (!Objects.equals(first.getSingleWeight(), schedule.getSingleWeight())) {
                        return "单重";
                    } else if (!Objects.equals(first.getColorCode(), schedule.getColorCode())) {
                        return "颜色编号";
                    } else if (!Objects.equals(first.getMouldNumber(), schedule.getMouldNumber())) {
                        return "模具编码";
                    } else if (!Objects.equals(first.getMaterialType(), schedule.getMaterialType())) {
                        return "料别";
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        if (diffField != null) {
            throw new ServiceException(diffField + "不一致");
        } else {
            erpProductionArrange.setSingleWeight(first.getSingleWeight());
            erpProductionArrange.setColorCode(first.getColorCode());
            erpProductionArrange.setMouldNumber(first.getMouldNumber());
            erpProductionArrange.setMaterialType(first.getMaterialType());
        }
        double productionWeight = schedules
                .stream()
                .filter(schedule -> schedule.getProductionWeight() != null)
                .mapToDouble(ErpProductionSchedule::getProductionWeight)
                .sum();
        long productionMouldCount = schedules
                .stream()
                .filter(schedule -> schedule.getProductionMouldCount() != null)
                .mapToLong(ErpProductionSchedule::getProductionMouldCount)
                .sum();
        long productionQuantity = schedules
                .stream()
                .filter(schedule -> schedule.getProductionQuantity() != null)
                .mapToLong(ErpProductionSchedule::getProductionQuantity)
                .sum();
        erpProductionArrange.setProductionWeight(productionWeight);
        erpProductionArrange.setProductionMouldCount(productionMouldCount);
        erpProductionArrange.setProductionQuantity(productionQuantity);

        if (0 >= insertErpProductionArrangeList(Collections.singletonList(erpProductionArrange))) {
            throw new ServiceException("添加排产失败");
        }
        return erpProductionScheduleService
                .attatchToArrange(erpProductionArrange.getId(), request.getScheduleIds());
    }

    @Override
    @Transactional
    public int updateErpProductionArrange(ErpProductionArrange erpProductionArrange) throws IOException {
        erpProductionArrange.setOperator(SecurityUtils.getUsername());
        Date completionTime = erpProductionArrange.getCompletionTime();
        if (completionTime != null) {
            erpProductionArrange.setCompletionTime(null);
        }
        if (erpProductionArrange.getPicture() != null) {
            String url = FileUploadUtils.upload(erpProductionArrange.getPicture());
            erpProductionArrange.setPictureUrl(url);
        }
        int result = baseMapper.updateById(erpProductionArrange);
        if (completionTime != null) {
            markArrangeComplete(erpProductionArrange.getId(), completionTime);
        }
        return result;
    }

    @Override
    @Transactional
    public void markArrangeComplete(Long id, Date completeDate) {
        if (completeDate.after(DateUtils.getLastSecondOfToday())) {
            throw new ServiceException("完成时间不能大于当前时间");
        }
        ErpProductionArrange erpProductionArrange = getById(id);
        if (erpProductionArrange == null) {
            throw new ServiceException("排产不存在");
        }
        if (erpProductionArrange.getCompletionTime() != null) {
            throw new ServiceException("排产已完成");
        }
        erpProductionArrange.setCompletionTime(completeDate);
        updateById(erpProductionArrange);

        // 找到所有相关布产
        LambdaQueryWrapper<ErpProductionSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ErpProductionSchedule::getArrangeId, id);
        List<ErpProductionSchedule> schedules = erpProductionScheduleService.list(wrapper);
        // 检查所有相关订单是否已完成
        Set<String> orderCodes = new HashSet<>();
        for (ErpProductionSchedule schedule : schedules) {
            orderCodes.add(schedule.getOrderCode());
        }
        for (String orderCode : orderCodes) {
            if (erpProductionScheduleService.isAllProduced(orderCode)) {
                if (erpPurchaseCollectionService.isAllPurchaseCompleted(orderCode)) {
                    erpOrdersService.updateOrderStatusAutomatic(orderCode, OrderStatus.MATERIAL_IN_INVENTORY);
                } else {
                    erpOrdersService.updateOrderStatusAutomatic(orderCode, OrderStatus.PRODUCTION_DONE_PURCHASING);
                }
            }
        }
    }
}
