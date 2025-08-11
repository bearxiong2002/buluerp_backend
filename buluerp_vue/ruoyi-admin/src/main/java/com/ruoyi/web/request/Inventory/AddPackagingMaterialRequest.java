package com.ruoyi.web.request.Inventory;

import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@ApiModel(value = "新增分包库存请求类")
public class AddPackagingMaterialRequest {
    @Excel(name = "订单编号", sort = 1)
    @NotBlank(message = "订单编号不能为空")
    @Size(max = 50, message = "订单编号长度不能超过50个字符")
    @ApiModelProperty(
            dataType = "String",
            value = "订单编号",
            required = true
    )
    private String orderCode;

    @Excel(name = "变更日期", dateFormat = "yyyy-MM-dd", sort = 2)
    @NotNull(message = "库存变更日期不能为空")
    @ApiModelProperty(
            dataType = "Date",
            value = "库存变更日期",
            required = true
    )
    private Date changeDate;

    //@Excel(name = "操作信息", sort = 3)
    @ApiModelProperty(
            dataType = "String",
            value = "操作信息",
            required = false
    )
    private String editAction;

    @Excel(name = "产品货号", sort = 3)
    @NotBlank(message = "产品货号不能为空")
    @ApiModelProperty(
            dataType = "String",
            value = "产品货号",
            required = true
    )
    private String productPartNumber;

    @Excel(name = "分包编号", sort = 4)
    @NotBlank(message = "分包编号不能为空")
    @ApiModelProperty(
            dataType = "String",
            value = "分包号",
            required = true
    )
    private String packagingNumber;

    @Excel(name = "出入库数量", sort = 5, prompt = "正数为入库，负数为出库")
    @NotNull(message = "出入库数量不能为空")
    @ApiModelProperty(
            dataType = "int",
            value = "出入库数量(包)",
            required = true
    )
    private Integer inOutQuantity;

    @Excel(name = "存储位置", sort = 6)
    @NotBlank(message = "存储位置不能为空")
    @ApiModelProperty(
            dataType = "String",
            value = "存储位置",
            required = true
    )
    private String storageLocation;

    @Excel(name = "备注信息", sort = 7)
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    @ApiModelProperty(
            dataType = "String",
            value = "备注信息",
            required = false
    )
    private String remarks;

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