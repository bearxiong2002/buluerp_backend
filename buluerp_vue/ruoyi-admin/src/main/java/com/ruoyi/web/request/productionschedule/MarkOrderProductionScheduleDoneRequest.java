package com.ruoyi.web.request.productionschedule;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

public class MarkOrderProductionScheduleDoneRequest {
    @ApiModelProperty("订单内部ID")
    @NotBlank(message = "订单内部ID不能为空")
    private String orderCode;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }
}
