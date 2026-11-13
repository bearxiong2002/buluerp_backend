package com.ruoyi.web.request.productionschedule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.web.domain.ErpProductionSchedule;
import io.swagger.annotations.ApiModelProperty;

public class ListProductionScheduleRequest extends ErpProductionSchedule {
    @ApiModelProperty(value = "订单编码精确查询")
    private String orderCodeExact;

    @ApiModelProperty(value = "是否包含已完成订单，默认不包含")
    private Boolean includeCompletedOrders = false;

    @JsonIgnore
    private Integer completedOrderStatus;

    public String getOrderCodeExact() {
        return orderCodeExact;
    }

    public void setOrderCodeExact(String orderCodeExact) {
        this.orderCodeExact = orderCodeExact;
    }

    public Boolean getIncludeCompletedOrders() {
        return includeCompletedOrders;
    }

    public void setIncludeCompletedOrders(Boolean includeCompletedOrders) {
        this.includeCompletedOrders = includeCompletedOrders;
    }

    public Integer getCompletedOrderStatus() {
        return completedOrderStatus;
    }

    public void setCompletedOrderStatus(Integer completedOrderStatus) {
        this.completedOrderStatus = completedOrderStatus;
    }
}