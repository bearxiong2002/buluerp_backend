package com.ruoyi.web.request.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "新增设计总表请求类")
public class AddDesignPatternsRequest {
    @ApiModelProperty(dataType = "Long",value = "订单号",required = true)
    private Long orderId;
    @ApiModelProperty(dataType = "Long",value = "产品号",required = true)
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
