package com.ruoyi.web.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "查询设计总表列表请求类")
public class ListDesignPatternsRequest {

    @ApiModelProperty(dataType = "Long",value = "产品号",required = false)
    private Long productId;

    @ApiModelProperty(dataType = "Long",value = "创建用户id",required = false)
    private Long createUserId;

    @ApiModelProperty(dataType = "Long",value = "订单id",required = false)
    private Long orderId;

    @ApiModelProperty(dataType = "Long",value = "pmc是否已确认",required = false)
    private Long confirm;

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getConfirm() {
        return confirm;
    }

    public void setConfirm(Long confirm) {
        this.confirm = confirm;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
