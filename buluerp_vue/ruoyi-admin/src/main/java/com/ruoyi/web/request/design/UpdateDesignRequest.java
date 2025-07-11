package com.ruoyi.web.request.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

/**
 * 创建设计总表请求
 */

@ApiModel(value = "修改造型表请求类")
public class UpdateDesignRequest {

    @ApiModelProperty(dataType = "Long",value = "需要修改的造型表id",required = true)
    private Long id;

    @ApiModelProperty(dataType = "Long",value = "造型表分组编号",required = false)
    private Long GroupId;

    /** 主设计编号 */
    @ApiModelProperty(dataType = "Long",value = "产品id",required = false)
    private Long productId;

    /** 模具编号，用于唯一标识模具 */
    @ApiModelProperty(dataType = "String",value = "模具编号",required = false)
    private String mouldNumber;

    /** LDD编号，与模具相关的编号 */
    @ApiModelProperty(dataType = "String",value = "LDD编号",required = false)
    private String lddNumber;

    /** 模具类别，如注塑模具、冲压模具等 */
    @ApiModelProperty(dataType = "String",value = "模具类别",required = false)
    private String mouldCategory;

    /** 模具ID，用于内部标识模具 */
    @ApiModelProperty(dataType = "String",value = "模具id",required = false)
    private String mouldId;

    /** 模具图片的URL链接，用于存储模具外观图片 */
    @ApiModelProperty(dataType = "file",value = "模具图片 ")
    private MultipartFile picture;

    /** 模具的颜色描述 */
    @ApiModelProperty(dataType = "String",value = "模具颜色描述")
    private String color;

    /** 模具生产的产品名称 */
    @ApiModelProperty(dataType = "String",value = "模具生产的产品名称",required = false)
    private String productName;

    /** 模具的数量 */
    @ApiModelProperty(dataType = "Long",value = "模具数量",required = false)
    private Long quantity;

    /** 模具的用料，如钢材、铝合金等 */
    @ApiModelProperty(dataType = "String",value = "模具的材料",required = false)
    private String material;

    /** 物料ID */
    @ApiModelProperty(dataType = "Long",value = "物料ID",required = false)
    private Long materialId;

    public Long getGroupId() {
        return GroupId;
    }

    public void setGroupId(Long groupId) {
        GroupId = groupId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getMouldNumber() {
        return mouldNumber;
    }

    public void setMouldNumber(String mouldNumber) {
        this.mouldNumber = mouldNumber;
    }

    public String getLddNumber() {
        return lddNumber;
    }

    public void setLddNumber(String lddNumber) {
        this.lddNumber = lddNumber;
    }

    public String getMouldCategory() {
        return mouldCategory;
    }

    public void setMouldCategory(String mouldCategory) {
        this.mouldCategory = mouldCategory;
    }

    public String getMouldId() {
        return mouldId;
    }

    public void setMouldId(String mouldId) {
        this.mouldId = mouldId;
    }

    public MultipartFile getPicture() {
        return picture;
    }

    public void setPicture(MultipartFile picture) {
        this.picture = picture;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}