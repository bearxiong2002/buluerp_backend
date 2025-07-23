package com.ruoyi.web.request.productionschedule;

import com.ruoyi.web.domain.ErpProductionSchedule;
import io.swagger.annotations.ApiModelProperty;

public class ListProductionScheduleRequest extends ErpProductionSchedule {
    @ApiModelProperty(value = "订单编码精确查询")
    private String orderCodeExact;

    public String getOrderCodeExact() {
        return orderCodeExact;
    }

    public void setOrderCodeExact(String orderCodeExact) {
        this.orderCodeExact = orderCodeExact;
    }
}
