package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.reflect.ReflectUtils;
import com.ruoyi.web.domain.ErpProductionArrange;
import com.ruoyi.web.domain.ErpProductionSchedule;
import com.ruoyi.web.exception.AutoLogException;
import com.ruoyi.web.mapper.ErpProductionArrangeMapper;
import com.ruoyi.web.mapper.ErpProductionScheduleMapper;
import com.ruoyi.web.request.arrange.AddProductionArrangeFromScheduleRequest;
import com.ruoyi.web.service.IErpProductionArrangeService;
import com.ruoyi.web.service.IErpProductionScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Service
public class ErpProductionArrangeServiceImpl
        extends ServiceImpl<ErpProductionArrangeMapper, ErpProductionArrange>
        implements IErpProductionArrangeService {
    @Autowired
    private IErpProductionScheduleService erpProductionScheduleService;

    @Override
    @Transactional
    public int insertErpProductionArrangeList(List<ErpProductionArrange> erpProductionArranges) throws IOException {
        int count = 0;
        for (ErpProductionArrange erpProductionArrange : erpProductionArranges) {
            erpProductionArrange.setCreationTime(DateUtils.getNowDate());
            erpProductionArrange.setOperator(SecurityUtils.getUsername());
            if (erpProductionArrange.getPicture() != null) {
                String url = FileUploadUtils.upload(erpProductionArrange.getPicture());
                erpProductionArrange.setPictureUrl(url);
            }
            count += baseMapper.insert(erpProductionArrange);
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFromSchedule(AddProductionArrangeFromScheduleRequest request) throws IOException {
        List<ErpProductionSchedule> schedules = erpProductionScheduleService
                .listByIds(request.getSheduleIds());
        if (schedules.size() != request.getSheduleIds().size()) {
            throw new ServiceException("部分布产ID无效");
        }
        if (schedules.stream().anyMatch(schedule -> schedule.getArrangeId() != null)) {
            throw new ServiceException("部分布产已排产");
        }
        ErpProductionArrange erpProductionArrange = new ErpProductionArrange();
        erpProductionArrange.setCreationTime(DateUtils.getNowDate());
        erpProductionArrange.setOperator(SecurityUtils.getUsername());
        if (request.getPictureFile() != null) {
            erpProductionArrange.setPictureUrl(
                    FileUploadUtils.upload(
                            request.getPictureFile()
                    )
            );
        } else {
            erpProductionArrange.setPictureUrl(request.getPictureUrl());
        }
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

        if (0 >= baseMapper.insert(erpProductionArrange)) {
            throw new ServiceException("添加排产失败");
        }
        return erpProductionScheduleService
                .attatchToArrange(erpProductionArrange.getId(), request.getSheduleIds());
    }

    @Override
    public int updateErpProductionArrange(ErpProductionArrange erpProductionArrange) throws IOException {
        erpProductionArrange.setOperator(SecurityUtils.getUsername());
        if (erpProductionArrange.getPicture() != null) {
            String url = FileUploadUtils.upload(erpProductionArrange.getPicture());
            erpProductionArrange.setPictureUrl(url);
        }
        return baseMapper.updateById(erpProductionArrange);
    }
}
