package com.ruoyi.web.request.design;

import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建设计总表请求
 */

@ApiModel(value = "新增造型表请求类")
public class AddDesignRequest {

    @NotNull(message = "分组编号不能为空")
    @Excel(name = "造型表的分组")
    @ApiModelProperty(dataType = "Long", value = "造型表分组编号", required = true)
    private Long groupId;

    @NotNull(message = "主设计编号不能为空")
    @Excel(name = "主设计编号")
    @ApiModelProperty(dataType = "Long", value = "主设计编号", required = true)
    private Long designPatternId;

    @NotBlank(message = "模具编号不能为空")
    @Excel(name = "模具编号")
    @ApiModelProperty(dataType = "String", value = "模具编号", required = true)
    private String mouldNumber;

    @NotBlank(message = "LDD编号不能为空")
    @Excel(name = "LDD编号")
    @ApiModelProperty(dataType = "String", value = "LDD编号", required = true)
    private String lddNumber;

    @NotBlank(message = "模具类别不能为空")
    @Excel(name = "模具类别")
    @ApiModelProperty(dataType = "String", value = "模具类别", required = true)
    private String mouldCategory;

    @NotBlank(message = "模具ID不能为空")
    @Excel(name = "模具ID")
    @ApiModelProperty(dataType = "String", value = "模具id", required = true)
    private String mouldId;

    @NotBlank(message = "产品名称不能为空")
    @Excel(name = "模具生产的产品名称")
    @ApiModelProperty(dataType = "String", value = "模具生产的产品名称", required = true)
    private String productName;

    @NotNull(message = "模具数量不能为空")
    @Excel(name = "模具数量")
    @ApiModelProperty(dataType = "Long", value = "模具数量", required = true)
    private Long quantity;

    @NotBlank(message = "模具用料不能为空")
    @Excel(name = "模具用料")
    @ApiModelProperty(dataType = "String", value = "模具的材料", required = true)
    private String material;

    // 非必填字段
    @Excel(name = "模具的颜色描述")
    @ApiModelProperty(dataType = "String", value = "模具颜色描述")
    private String color;

    @ApiModelProperty(dataType = "String", value = "模具图片")
    private MultipartFile picture;


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