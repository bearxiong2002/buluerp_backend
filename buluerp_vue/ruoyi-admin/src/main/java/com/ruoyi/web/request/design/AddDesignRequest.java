package com.ruoyi.web.request.design;

import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

/**
 * 创建设计总表请求
 */

@ApiModel(value = "新增造型表请求类")
public class AddDesignRequest {

    @Excel(name = "造型表的分组")
    @ApiModelProperty(dataType = "Long",value = "造型表分组编号",required = true)
    private Long groupId;

    /** 主设计编号 */
    @Excel(name = "主设计编号")
    @ApiModelProperty(dataType = "Long",value = "主设计编号",required = true)
    private Long designPatternId;

    /** 模具编号，用于唯一标识模具 */
    @Excel(name = "模具编号")
    @ApiModelProperty(dataType = "String",value = "模具编号",required = true)
    private String mouldNumber;

    /** LDD编号，与模具相关的编号 */
    @Excel(name = "LDD编号")
    @ApiModelProperty(dataType = "String",value = "LDD编号",required = true)
    private String lddNumber;

    /** 模具类别，如注塑模具、冲压模具等 */
    @Excel(name = "模具类别")
    @ApiModelProperty(dataType = "String",value = "模具类别",required = true)
    private String mouldCategory;

    /** 模具ID，用于内部标识模具 */
    @Excel(name = "模具ID")
    @ApiModelProperty(dataType = "String",value = "模具id",required = true)
    private String mouldId;

    /** 模具图片的URL链接，用于存储模具外观图片 */
    @Excel(name = "模具图片")
    @ApiModelProperty(dataType = "String",value = "模具图片 ")
    private MultipartFile picture;

    /** 模具的颜色描述 */
    @Excel(name = "模具的颜色描述")
    @ApiModelProperty(dataType = "String",value = "模具颜色描述")
    private String color;

    /** 模具生产的产品名称 */
    @Excel(name = "模具生产的产品名称")
    @ApiModelProperty(dataType = "String",value = "模具生产的产品名称",required = true)
    private String productName;

    /** 模具的数量 */
    @Excel(name = "模具数量")
    @ApiModelProperty(dataType = "Long",value = "模具数量",required = true)
    private Long quantity;

    /** 模具的用料，如钢材、铝合金等 */
    @Excel(name = "模具用料")
    @ApiModelProperty(dataType = "String",value = "模具的材料",required = true)
    private String material;


    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getDesignPatternId() {
        return designPatternId;
    }

    public void setDesignPatternId(Long designPatternId) {
        this.designPatternId = designPatternId;
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
}