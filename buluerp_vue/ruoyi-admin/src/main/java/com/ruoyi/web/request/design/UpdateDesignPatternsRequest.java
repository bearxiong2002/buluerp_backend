package com.ruoyi.web.request.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "修改设计总表请求类")
public class UpdateDesignPatternsRequest {
    @ApiModelProperty(dataType = "Long",value = "需要修改的设计表id",required = true)
    private Long id;
    @ApiModelProperty(dataType = "String",value = "订单内部编号",required = false)
    private String orderId;
    @ApiModelProperty(dataType = "String",value = "产品内部编号",required = false)
    private String productId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
