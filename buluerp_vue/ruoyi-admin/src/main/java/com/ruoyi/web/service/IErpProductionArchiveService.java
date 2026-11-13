package com.ruoyi.web.service;

import com.ruoyi.web.domain.ErpProductionArrange;
import com.ruoyi.web.request.order.ListOrderRequest;
import com.ruoyi.web.request.productionschedule.ListProductionScheduleRequest;
import com.ruoyi.web.result.ProductionArchiveOrderResult;
import com.ruoyi.web.result.ProductionScheduleResult;

import java.util.List;

/**
 * 已完成订单归档只读查询。
 */
public interface IErpProductionArchiveService {
    List<ProductionArchiveOrderResult> listArchiveOrders(ListOrderRequest request);

    List<ProductionArchiveOrderResult> listArchiveOrdersByIds(Long[] ids);

    String requireCompletedOrderCode(String orderCode);

    List<ProductionScheduleResult> listSchedulesByOrderCode(String orderCode, ListProductionScheduleRequest request);

    List<ErpProductionArrange> listArrangesByOrderCode(String orderCode, ErpProductionArrange filter);
}