package com.ruoyi.web.request.purchase;

import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

@ApiModel("查询采购订单请求")
public class ListPurchaseOrderRequest extends BaseEntity {

    @ApiModelProperty(value = "采购单ID(可选)",required = false)
    private Integer id;
    @ApiModelProperty(value = "采购计划ID(可选)",required = false)
    private Integer purchaseId;
    @ApiModelProperty(dataType = "DateTime",value = "创建时间起始",required = false)
    private LocalDateTime createTimeFrom;
    @ApiModelProperty(dataType = "DateTime",value = "创建时间终止",required = false)
    private LocalDateTime createTimeTo;
    private Double amount;
    @ApiModelProperty(dataType = "String",value = "创建用户",required = false)
    private String createUser;
//    @ApiModelProperty(value = "订单金额",required = false)
//    private Double amount;

    // Getters and Setters

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreateTimeFrom() {
        return createTimeFrom;
    }

    public void setCreateTimeFrom(LocalDateTime createTimeFrom) {
        this.createTimeFrom = createTimeFrom;
    }

    public LocalDateTime getCreateTimeTo() {
        return createTimeTo;
    }

    public void setCreateTimeTo(LocalDateTime createTimeTo) {
        this.createTimeTo = createTimeTo;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

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