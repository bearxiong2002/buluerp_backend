package com.ruoyi.web.request.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@ApiModel(value = "修改产品请求类")
public class UpdateProductRequest {
    @ApiModelProperty(dataType = "Long",value = "需要修改的产品id",required = true)
    private Long id;
    @ApiModelProperty(dataType = "String",value = "产品名称",required = false)
    private String name;
    @ApiModelProperty(dataType = "File",value = "产品图片",required = false)
    private MultipartFile picture;
    @ApiModelProperty(dataType = "int",value = "设计状态 1=已完成,0=未完成",required = false)
    private Long designStatus;

    @ApiModelProperty(dataType = "Long",value = "物料id",required = false)
    private Long materialId;

    @ApiModelProperty(dataType = "List<Long>",value = "物料id列表（批量绑定）",required = false)
    private List<Long> materialIds;

    @ApiModelProperty(dataType = "Integer",value = "是否删除图片,1=删除",required = false)
    private Integer deletePicture;

    public Integer getDeletePicture() {
        return deletePicture;
    }

    public void setDeletePicture(Integer deletePicture) {
        this.deletePicture = deletePicture;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public List<Long> getMaterialIds() {
        return materialIds;
    }

    public void setMaterialIds(List<Long> materialIds) {
        this.materialIds = materialIds;
    }

    public Long getDesignStatus() {
        return designStatus;
    }

    public void setDesignStatus(Long designStatus) {
        this.designStatus = designStatus;
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
