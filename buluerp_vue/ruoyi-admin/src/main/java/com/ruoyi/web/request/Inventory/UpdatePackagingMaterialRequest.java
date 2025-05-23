package com.ruoyi.web.request.Inventory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel(value = "修改分包库存请求类")
public class UpdatePackagingMaterialRequest {
    @ApiModelProperty(
            dataType = "Long",
            value = "记录ID（必填）",
            required = true
    )
    private Long id;

    @ApiModelProperty(
            dataType = "String",
            value = "订单编号",
            required = false
    )
    private String orderCode;

    @ApiModelProperty(
            dataType = "Date",
            value = "变更日期",
            required = false
    )
    private Date changeDate;

    @ApiModelProperty(
            dataType = "String",
            value = "操作信息",
            required = false
    )
    private String editAction;

    @ApiModelProperty(
            dataType = "String",
            value = "产品部件号",
            required = false
    )
    private String productPartNumber;

    @ApiModelProperty(
            dataType = "String",
            value = "包装编号",
            required = false
    )
    private String packagingNumber;

    @ApiModelProperty(
            dataType = "int",
            value = "出入库数量",
            required = false
    )
    private Integer inOutQuantity;

    @ApiModelProperty(
            dataType = "String",
            value = "存储位置",
            required = false
    )
    private String storageLocation;

    @ApiModelProperty(
            dataType = "String",
            value = "备注信息",
            required = false
    )
    private String remarks;

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

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public String getEditAction() {
        return editAction;
    }

    public void setEditAction(String editAction) {
        this.editAction = editAction;
    }

    public String getProductPartNumber() {
        return productPartNumber;
    }

    public void setProductPartNumber(String productPartNumber) {
        this.productPartNumber = productPartNumber;
    }

    public String getPackagingNumber() {
        return packagingNumber;
    }

    public void setPackagingNumber(String packagingNumber) {
        this.packagingNumber = packagingNumber;
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
}