package com.ruoyi.web.request.product;

import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

@ApiModel(value = "新增产品请求类")
public class AddProductRequest {

    @Excel(name ="产品名")
    @ApiModelProperty(dataType = "Long",value = "产品名称",required = true)
    private String name;
    @ApiModelProperty(dataType = "File",value = "产品图片",required = true)
    private MultipartFile picture;

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
