package com.ruoyi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.web.domain.ErpProductionSchedule;
import com.ruoyi.web.request.productionschedule.AddProductionScheduleFromMaterialRequest;

import java.io.IOException;
import java.util.List;

public interface IErpProductionScheduleService extends IService<ErpProductionSchedule> {
    // 订单布产计划是否制定完成
    boolean isAllScheduled(String orderCode);

    // 标记布产计划已经制定完成
    void markAllScheduled(String orderCode);

    // 布产对应排产是否完成
    boolean isProduced(Long scheduleId);

    // 布产是否已经开始排产
    boolean isProducing(Long scheduleId);

    // 订单当前所有布产是否完成排产
    boolean isCurrentlyProduced(String orderCode);

    // 订单当前所有布产是否开始排产
    boolean isCurrentlyProducing(String orderCode);

    // 订单整个生产流程是否已经完成
    boolean isAllProduced(String orderCode);

    // 是否处于等待已开始的排产完成状态
    boolean isAllProducing(String orderCode);

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
