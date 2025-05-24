package com.ruoyi.web.request.Inventory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import java.util.Date;

@ApiModel(value = "成品库存查询请求类")
public class ListProductInventoryRequest {
    @ApiModelProperty(
            dataType = "Long",
            value = "记录ID"
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
            dataType = "String",
            value = "操作人"
    )
    private String operator;

    @ApiModelProperty(
            dataType = "Integer",
            value = "出入库数量"
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
            dataType = "LocalDateTime",
            value = "创建时间起始"
    )
    private LocalDateTime createTimeFrom;

    @ApiModelProperty(
            dataType = "LocalDateTime",
            value = "创建时间终止"
    )
    private LocalDateTime createTimeTo;

    @ApiModelProperty(
            dataType = "Date",
            value = "库存变更日期起始"
    )
    private Date changeDateFrom;

    @ApiModelProperty(
            dataType = "Date",
            value = "库存变更日期结束"
    )
    private Date changeDateTo;

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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
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
}