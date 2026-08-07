package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.web.domain.ErpProductionArrange;
import com.ruoyi.web.domain.ErpProductionSchedule;
import com.ruoyi.web.enums.OrderStatus;
import com.ruoyi.web.mapper.ErpProductionArrangeMapper;
import com.ruoyi.web.request.arrange.AddProductionArrangeFromScheduleRequest;
import com.ruoyi.web.result.ProductionScheduleResult;
import com.ruoyi.web.service.IErpOrdersService;
import com.ruoyi.web.service.IErpProductionArrangeService;
import com.ruoyi.web.service.IErpProductionScheduleService;
import com.ruoyi.web.service.IErpPurchaseCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.web.annotation.MarkNotificationsAsRead;

import java.io.IOException;
import java.util.*;

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
    @MarkNotificationsAsRead(businessType = "PRODUCTION_SCHEDULE", businessIdsExpression = "#request.scheduleId")
    public int insertFromSchedule(AddProductionArrangeFromScheduleRequest request) throws IOException {
        ProductionScheduleResult schedule = erpProductionScheduleService
                .getResultById(request.getScheduleId());
        if (schedule == null) {
            throw new ServiceException("布产ID无效");
        }
        if (schedule.getArrangeId() != null) {
            throw new ServiceException("此布产已排产");
        }
        ErpProductionArrange erpProductionArrange = new ErpProductionArrange();
        erpProductionArrange.setPicture(request.getPictureFile());
        // 用户未手动上传图片时，默认沿用布产的图片
        if (request.getPictureFile() == null && StringUtils.isEmpty(request.getPictureUrl())) {
            erpProductionArrange.setPictureUrl(schedule.getPictureUrl());
        } else {
            erpProductionArrange.setPictureUrl(request.getPictureUrl());
        }
        erpProductionArrange.setMouldOutput(request.getMouldOutput());
        erpProductionArrange.setScheduledTime(request.getScheduledTime());
        erpProductionArrange.setCompletionTime(request.getCompletionTime());
        erpProductionArrange.setRemarks(request.getRemarks());

        erpProductionArrange.setSingleWeight(schedule.getSingleWeight());
        erpProductionArrange.setColorCode(schedule.getColorCode());
        erpProductionArrange.setMouldNumber(schedule.getMouldNumber());
        erpProductionArrange.setMaterialType(schedule.getMaterialType());
        erpProductionArrange.setProductionWeight(schedule.getProductionWeight());
        erpProductionArrange.setProductionMouldCount(schedule.getProductionMouldCount() == null
                ? null
                : schedule.getProductionMouldCount().longValue());
        erpProductionArrange.setProductionQuantity(schedule.getProductionQuantity() == null
                ? null
                : schedule.getProductionQuantity().longValue());

        if (0 >= insertErpProductionArrangeList(Collections.singletonList(erpProductionArrange))) {
            throw new ServiceException("添加排产失败");
        }
        return erpProductionScheduleService
                .attachToArrange(erpProductionArrange.getId(), Collections.singletonList(request.getScheduleId()));
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
    public void removeChecked(Long id) {
        LambdaQueryWrapper<ErpProductionSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ErpProductionSchedule::getArrangeId, id);
        if (erpProductionScheduleService.count(wrapper) > 0) {
            throw new ServiceException("部分布产已添加到排产" + id + "，不能删除");
        }
        if (!removeById(id)) {
            throw new ServiceException("指定排产不存在");
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
