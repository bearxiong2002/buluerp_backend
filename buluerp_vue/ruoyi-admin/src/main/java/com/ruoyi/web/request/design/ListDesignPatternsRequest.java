package com.ruoyi.web.request.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "查询设计总表列表请求类")
public class ListDesignPatternsRequest {

    @ApiModelProperty(dataType = "Long",value = "产品号",required = false)
    private String productId;

    @ApiModelProperty(dataType = "Long",value = "创建用户id",required = false)
    private Long createUserId;

    @ApiModelProperty(dataType = "Long",value = "订单id",required = false)
    private Long orderId;

    @ApiModelProperty(dataType = "Long",value = "pmc是否已确认",required = false)
    private Long confirm;

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
