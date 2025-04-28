package com.ruoyi.web.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "修改设计总表请求类")
public class UpdateDesignPatternsRequest {
    @ApiModelProperty(dataType = "Long",value = "订单号",required = false)
    private Long orderId;
    @ApiModelProperty(dataType = "Long",value = "产品号",required = false)
    private Long productId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
