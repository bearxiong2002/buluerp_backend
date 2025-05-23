package com.ruoyi.web.request.Inventory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel(value = "更新成品库存请求类")
public class UpdateProductInventoryRequest {
    @ApiModelProperty(
            dataType = "Long",
            value = "记录ID（必填）",
            required = true
    )
    private Long id;

    @ApiModelProperty(
            dataType = "String",
            value = "订单编号"
    )
    private String orderCode;

    @ApiModelProperty(
            dataType = "String",
            value = "产品货号"
    )
    private String productPartNumber;

    @ApiModelProperty(
            dataType = "Integer",
            value = "出入库数量（正数入库，负数出库）"
    )
    private Integer inOutQuantity;

    @ApiModelProperty(
            dataType = "String",
            value = "存储位置"
    )
    private String storageLocation;

    @ApiModelProperty(
            dataType = "String",
            value = "备注信息"
    )
    private String remarks;

    @ApiModelProperty(
            dataType = "Date",
            value = "库存变更日期",
            required = false
    )
    private Date changeDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getProductPartNumber() {
        return productPartNumber;
    }

    public void setProductPartNumber(String productPartNumber) {
        this.productPartNumber = productPartNumber;
    }

    public Integer getInOutQuantity() {
        return inOutQuantity;
    }

    public void setInOutQuantity(Integer inOutQuantity) {
        this.inOutQuantity = inOutQuantity;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }
}