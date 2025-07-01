package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.web.domain.ErpProductionSchedule;
import com.ruoyi.web.enums.AuditTypeEnum;
import com.ruoyi.web.mapper.ErpProductionScheduleMapper;
import com.ruoyi.web.service.IErpAuditRecordService;
import com.ruoyi.web.service.IErpAuditSwitchService;
import com.ruoyi.web.service.IErpProductionScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
public class ErpProductionScheduleServiceImpl
        extends ServiceImpl<ErpProductionScheduleMapper, ErpProductionSchedule>
        implements IErpProductionScheduleService {

    @Autowired
    private IErpAuditRecordService auditRecordService;

    @Autowired
    private IErpAuditSwitchService auditSwitchService;
    @Override
    @Transactional
    public int insertErpProductionSchedule(ErpProductionSchedule erpProductionSchedule) throws IOException {
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
        if (0 == getBaseMapper().insert(erpProductionSchedule)) {
            throw new ServiceException("操作失败");
        }
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
    public int updateErpProductionSchedule(ErpProductionSchedule erpProductionSchedule) throws IOException {
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
            
            auditRecordService.handleProductionScheduleStatusChange(erpProductionSchedule, erpProductionSchedule.getStatus().intValue());
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
            getBaseMapper().clearProductionScheduleMaterialIds(id);
        }
        return getBaseMapper().deleteBatchIds(ids);
    }
}
