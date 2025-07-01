package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.ErpProductionSchedule;

import java.io.IOException;
import java.util.List;

public interface IErpProductionScheduleService extends IService<ErpProductionSchedule> {
    int insertErpProductionSchedule(ErpProductionSchedule erpProductionSchedule) throws IOException;

    int updateErpProductionSchedule(ErpProductionSchedule erpProductionSchedule) throws IOException;

    int attatchToArrange(Long productionArrangeId, List<Long> productionScheduleIds);

    List<Long> getProductionScheduleMaterialIds(Long productionScheduleId);

    int removeErpProductionScheduleList(List<Long> ids);
}
