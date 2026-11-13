package com.ruoyi.web.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.web.domain.ErpOrders;
import com.ruoyi.web.domain.ErpOrdersProduct;
import com.ruoyi.web.domain.ErpProductionArrange;
import com.ruoyi.web.domain.ErpProducts;
import com.ruoyi.web.enums.OrderStatus;
import com.ruoyi.web.mapper.ErpProductionScheduleMapper;
import com.ruoyi.web.request.order.ListOrderRequest;
import com.ruoyi.web.request.productionschedule.ListProductionScheduleRequest;
import com.ruoyi.web.result.ProductionArchiveOrderResult;
import com.ruoyi.web.result.ProductionArchiveRecordCount;
import com.ruoyi.web.result.ProductionScheduleResult;
import com.ruoyi.web.service.IErpOrdersService;
import com.ruoyi.web.service.IErpProductionArchiveService;
import com.ruoyi.web.service.IErpProductionArrangeService;
import com.ruoyi.web.service.IErpProductionScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ErpProductionArchiveServiceImpl implements IErpProductionArchiveService {
    @Autowired
    private IErpOrdersService erpOrdersService;

    @Autowired
    private IErpProductionScheduleService erpProductionScheduleService;

    @Autowired
    private IErpProductionArrangeService erpProductionArrangeService;

    @Autowired
    private ErpProductionScheduleMapper erpProductionScheduleMapper;

    @Override
    public List<ProductionArchiveOrderResult> listArchiveOrders(ListOrderRequest request) {
        Integer completedStatus = getCompletedStatus();
        request.setStatus(null);
        request.setStatusList(Collections.singletonList(completedStatus));
        return toArchiveResults(erpOrdersService.selectErpOrdersList(request), completedStatus);
    }

    @Override
    public List<ProductionArchiveOrderResult> listArchiveOrdersByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return Collections.emptyList();
        }
        Integer completedStatus = getCompletedStatus();
        return toArchiveResults(erpOrdersService.selectErpOrdersListByIds(ids), completedStatus);
    }

    @Override
    public List<ProductionScheduleResult> listSchedulesByOrderCode(
            String orderCode,
            ListProductionScheduleRequest request
    ) {
        request.setOrderCode(null);
        request.setOrderCodeExact(orderCode);
        request.setIncludeCompletedOrders(true);
        return erpProductionScheduleService.listResult(request);
    }

    @Override
    public List<ErpProductionArrange> listArrangesByOrderCode(
            String orderCode,
            ErpProductionArrange filter
    ) {
        return erpProductionArrangeService.listArchivedByOrderCode(orderCode, filter);
    }

    private List<ProductionArchiveOrderResult> toArchiveResults(
            List<ErpOrders> orders,
            Integer completedStatus
    ) {
        List<ErpOrders> completedOrders = orders.stream()
                .filter(order -> Objects.equals(order.getStatus(), completedStatus))
                .collect(Collectors.toList());
        if (completedOrders.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> orderCodes = completedOrders.stream()
                .map(ErpOrders::getInnerId)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        Map<String, ProductionArchiveRecordCount> countMap = new HashMap<>();
        if (!orderCodes.isEmpty()) {
            for (ProductionArchiveRecordCount count : erpProductionScheduleMapper.selectArchiveRecordCounts(orderCodes)) {
                countMap.put(count.getOrderCode(), count);
            }
        }

        return completedOrders.stream()
                .map(order -> toArchiveResult(order, countMap.get(order.getInnerId())))
                .collect(Collectors.toList());
    }

    private ProductionArchiveOrderResult toArchiveResult(
            ErpOrders order,
            ProductionArchiveRecordCount count
    ) {
        ProductionArchiveOrderResult result = new ProductionArchiveOrderResult();
        result.setId(order.getId());
        result.setInnerId(order.getInnerId());
        result.setOuterId(order.getOuterId());
        result.setCustomerName(order.getCustomerName());
        result.setQuantity(order.getQuantity());
        result.setDeliveryTime(order.getDeliveryTime());
        result.setStatus(order.getStatus());
        result.setStatusLabel(erpOrdersService.getStatusLabel(order.getStatus()));
        result.setOperator(order.getOperator());
        result.setCreateTime(order.getCreateTime());

        ErpProducts product = order.getProduct();
        List<ErpOrdersProduct> orderProducts = order.getProducts();
        if (product == null && orderProducts != null && !orderProducts.isEmpty()) {
            product = orderProducts.get(0).getProduct();
        }
        if (product != null) {
            result.setProductName(product.getName());
            result.setProductCode(product.getInnerId());
        } else {
            result.setProductCode(order.getProductId());
        }

        result.setScheduleCount(count == null ? 0L : count.getScheduleCount());
        result.setArrangeCount(count == null ? 0L : count.getArrangeCount());
        return result;
    }

    @Override
    public String requireCompletedOrderCode(String orderCode) {
        String defaultOrderCode = erpOrdersService.requireDefaultInnerCode(orderCode);
        ErpOrders order = erpOrdersService.selectByOrderCode(defaultOrderCode);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        if (!Objects.equals(order.getStatus(), getCompletedStatus())) {
            throw new ServiceException("订单尚未完成，不能查看归档数据");
        }
        return defaultOrderCode;
    }

    private Integer getCompletedStatus() {
        return erpOrdersService.getStatusValue(OrderStatus.COMPLETED.getLabel());
    }
}