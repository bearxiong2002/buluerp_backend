package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.web.domain.ErpProductionSchedule;
import com.ruoyi.web.mapper.ErpProductionScheduleMapper;
import com.ruoyi.web.service.IErpProductsScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ErpProductsScheduleServiceImpl
        extends ServiceImpl<ErpProductionScheduleMapper, ErpProductionSchedule>
        implements IErpProductsScheduleService {
    @Override
    @Transactional
    public int insertErpProductionSchedule(ErpProductionSchedule erpProductionSchedule) throws IOException {
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
        return 1;
    }

    @Override
    public int updateErpProductionSchedule(ErpProductionSchedule erpProductionSchedule) throws IOException {
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
        return 1;
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
