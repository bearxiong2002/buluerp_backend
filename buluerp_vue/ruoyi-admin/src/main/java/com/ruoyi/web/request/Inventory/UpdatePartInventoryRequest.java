package com.ruoyi.web.request.Inventory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel(value = "更新胶件库存请求类")
public class UpdatePartInventoryRequest {

    @ApiModelProperty(
            dataType = "Long",
            value = "记录ID（必填）",
            required = true
    )
    private Long id;

    @ApiModelProperty(dataType = "String", value = "订单编号")
    private String orderCode;

    @ApiModelProperty(dataType = "String", value = "模具编号")
    private String mouldNumber;

    @ApiModelProperty(dataType = "String", value = "颜色代码")
    private String colorCode;

    @ApiModelProperty(dataType = "Integer", value = "出入库数量")
    private Integer inOutQuantity;

    @ApiModelProperty(dataType = "String", value = "备注信息")
    private String remarks;

    @ApiModelProperty(
            dataType = "Date",
            value = "库存变更日期"
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

    public String getMouldNumber() {
        return mouldNumber;
    }

    public void setMouldNumber(String mouldNumber) {
        this.mouldNumber = mouldNumber;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public Integer getInOutQuantity() {
        return inOutQuantity;
    }

    public void setInOutQuantity(Integer inOutQuantity) {
        this.inOutQuantity = inOutQuantity;
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