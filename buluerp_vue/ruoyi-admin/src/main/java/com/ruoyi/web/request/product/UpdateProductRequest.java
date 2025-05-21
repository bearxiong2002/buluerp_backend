package com.ruoyi.web.request.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@ApiModel(value = "修改产品请求类")
public class UpdateProductRequest {
    @ApiModelProperty(dataType = "Long",value = "需要修改的产品id",required = true)
    private Long id;
    @ApiModelProperty(dataType = "Long",value = "产品名称",required = false)
    private String name;
    @ApiModelProperty(dataType = "File",value = "产品图片",required = false)
    private MultipartFile picture;

    @ApiModelProperty(dataType = "List<Integer>",value = "物料id列表",required = false)
    private List<Integer> materialIds;

    public List<Integer> getMaterialIds() {
        return materialIds;
    }

    public void setMaterialIds(List<Integer> materialIds) {
        this.materialIds = materialIds;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
