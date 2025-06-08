package com.ruoyi.web.request.Inventory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


import java.util.Date;

@ApiModel(value = "分包库存查询请求类")
public class ListPackagingMaterialRequest {
    @ApiModelProperty(
            dataType = "Long",
            value = "记录ID",
            required = false
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
            value = "变更日期起始",
            required = false
    )
    private Date changeDateFrom;

    @ApiModelProperty(
            dataType = "Date",
            value = "变更日期结束",
            required = false
    )
    private Date changeDateTo;

    @ApiModelProperty(
            dataType = "String",
            value = "产品货号",
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
            value = "操作人",
            required = false
    )
    private String operator;

    @ApiModelProperty(
            dataType = "String",
            value = "备注信息",
            required = false
    )
    private String remarks;

    @ApiModelProperty(
            dataType = "Date",
            value = "创建时间起始",
            required = false
    )
    private Date createTimeFrom;

    @ApiModelProperty(
            dataType = "Date",
            value = "创建时间终止",
            required = false
    )
    private Date createTimeTo;

    @ApiModelProperty(
            dataType = "String",
            value = "操作信息",
            required = false
    )
    private String editAction;

    public String getEditAction() {
        return editAction;
    }

    public void setEditAction(String editAction) {
        this.editAction = editAction;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getChangeDateFrom() {
        return changeDateFrom;
    }

    public void setChangeDateFrom(Date changeDateFrom) {
        this.changeDateFrom = changeDateFrom;
    }

    public Date getChangeDateTo() {
        return changeDateTo;
    }

    public void setChangeDateTo(Date changeDateTo) {
        this.changeDateTo = changeDateTo;
    }

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

    public Date getCreateTimeFrom() {
        return createTimeFrom;
    }

    public void setCreateTimeFrom(Date createTimeFrom) {
        this.createTimeFrom = createTimeFrom;
    }

    public Date getCreateTimeTo() {
        return createTimeTo;
    }

    public void setCreateTimeTo(Date createTimeTo) {
        this.createTimeTo = createTimeTo;
    }
}