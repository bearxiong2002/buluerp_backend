package com.ruoyi.web.request.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@ApiModel(value = "新增产品请求类")
public class AddProductRequest {
    @ApiModelProperty(dataType = "Long",value = "产品名称",required = true)
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
