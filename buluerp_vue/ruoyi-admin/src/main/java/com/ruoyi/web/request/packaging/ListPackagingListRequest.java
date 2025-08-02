package com.ruoyi.web.request.packaging;

import com.ruoyi.web.domain.ErpPackagingList;
import io.swagger.annotations.ApiModelProperty;

public class ListPackagingListRequest extends ErpPackagingList {
    @ApiModelProperty("订单编号(精确匹配) [list]")
    private String orderCodeExact;

    public String getOrderCodeExact() {
        return orderCodeExact;
    }

    public void setOrderCodeExact(String orderCodeExact) {
        this.orderCodeExact = orderCodeExact;
    }
}
