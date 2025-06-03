package com.ruoyi.web.request.product;

import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel(value = "新增产品请求类")
public class AddProductRequest {

    @Excel(name = "订单id")
    @ApiModelProperty(dataType = "int",value = "订单id",required = true)
    @NotNull
    private Integer orderId;
    @Excel(name ="产品名")
    @ApiModelProperty(dataType = "Long",value = "产品名称",required = true)
    @NotBlank
    private String name;
    @ApiModelProperty(dataType = "File",value = "产品图片",required = true)
    private MultipartFile picture;

    public List<Integer> getMaterialIds() {
        return materialIds;
    }

    public void setMaterialIds(List<Integer> materialIds) {
        this.materialIds = materialIds;
    }
    @ApiModelProperty(dataType = "List<Integer>",value = "物料id列表",required = true)
    private List<Integer> materialIds;
    @Excel(name ="物料id列表")
    @NotBlank
    private String materialString;

    private Integer rowNumber;

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getMaterialString() {
        return materialString;
    }

    public void setMaterialString(String materialString) {
        this.materialString = materialString;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MultipartFile getPicture() {
        return picture;
    }

    public void setPicture(MultipartFile picture) {
        this.picture = picture;
    }
}
