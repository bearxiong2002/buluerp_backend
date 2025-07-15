package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.ErpProductionSchedule;
import com.ruoyi.web.request.productionschedule.AddProductionScheduleFromMaterialRequest;

import java.io.IOException;
import java.util.List;

public interface IErpProductionScheduleService extends IService<ErpProductionSchedule> {
    int insertFromMaterial(AddProductionScheduleFromMaterialRequest request) throws IOException;

    int insertErpProductionSchedule(ErpProductionSchedule erpProductionSchedule) throws IOException;

    int updateErpProductionSchedule(ErpProductionSchedule erpProductionSchedule) throws IOException;

    int attatchToArrange(Long productionArrangeId, List<Long> productionScheduleIds);

    List<Long> getProductionScheduleMaterialIds(Long productionScheduleId);

    int removeErpProductionScheduleList(List<Long> ids);

    /**
     * 在审核通过后，直接应用新的状态，不触发额外的审核流程
     * @param erpProductionSchedule 包含新状态的布产计划对象
     */
    void applyApprovedStatus(ErpProductionSchedule erpProductionSchedule);
}
