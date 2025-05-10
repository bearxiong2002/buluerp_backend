package com.ruoyi.web.request.purchase;

import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("查询采购订单请求")
public class ListPurchaseOrderRequest extends BaseEntity {

    @ApiModelProperty(value = "采购单ID(可选)",required = false)
    private Integer id;

    @ApiModelProperty(value = "采购计划ID(可选)",required = false)
    private Integer purchaseId;

//    @ApiModelProperty(value = "订单金额",required = false)
//    private Double amount;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Integer purchaseId) {
        this.purchaseId = purchaseId;
    }

}