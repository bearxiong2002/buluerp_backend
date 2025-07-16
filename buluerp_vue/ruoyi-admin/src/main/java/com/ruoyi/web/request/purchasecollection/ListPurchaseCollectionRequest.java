package com.ruoyi.web.request.purchasecollection;

import com.ruoyi.web.domain.ErpPurchaseCollection;
import io.swagger.annotations.ApiModelProperty;

public class ListPurchaseCollectionRequest extends ErpPurchaseCollection {
    @ApiModelProperty(value = "订单内部ID(准确匹配)")
    private String orderCodeExact;

    public String getOrderCodeExact() {
        return orderCodeExact;
    }

    public void setOrderCodeExact(String orderCodeExact) {
        this.orderCodeExact = orderCodeExact;
    }
}
