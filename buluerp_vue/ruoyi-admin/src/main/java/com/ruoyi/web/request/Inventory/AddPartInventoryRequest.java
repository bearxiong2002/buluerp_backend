package com.ruoyi.web.request.Inventory;

import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@ApiModel(value = "新增胶件库存请求类")
public class AddPartInventoryRequest {

    @Excel(name = "订单编号", sort = 1)
    @NotBlank(message = "订单编号不能为空")
    @ApiModelProperty(
            dataType = "String",
            value = "订单编号",
            required = true
    )
    private String orderCode;

    @Excel(name = "模具编号", sort = 2)
    @NotBlank(message = "模具编号不能为空")
    @ApiModelProperty(
            dataType = "String",
            value = "模具编号",
            required = true
    )
    private String mouldNumber;

    @Excel(name = "颜色代码", sort = 3)
    @NotBlank(message = "颜色代码不能为空")
    @ApiModelProperty(
            dataType = "String",
            value = "颜色代码",
            required = true
    )
    private String colorCode;

    @Excel(name = "出入库数量", sort = 4, prompt = "正数为入库，负数为出库")
    @NotNull(message = "出入库数量不能为空")
    @ApiModelProperty(
            dataType = "Integer",
            value = "出入库数量",
            required = true
    )
    private Integer inOutQuantity;

    @Excel(name = "备注信息", sort = 5)
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    @ApiModelProperty(
            dataType = "String",
            value = "备注信息",
            required = false
    )
    private String remarks;

    @Excel(name = "变更日期", dateFormat = "yyyy-MM-dd", sort = 6)
    @NotNull(message = "库存变更日期不能为空")
    @ApiModelProperty(
            dataType = "Date",
            value = "库存变更日期",
            required = true
    )
    private Date changeDate;

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