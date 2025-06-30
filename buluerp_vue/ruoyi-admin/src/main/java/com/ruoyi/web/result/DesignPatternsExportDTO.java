package com.ruoyi.web.result;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.web.result.DesignPatternsResult;

public class DesignPatternsExportDTO {

    @Excel(name = "产品编号")
    private Long id;

    @Excel(name = "模具编号")
    private String mouldNumbers;  // Set<String> -> String

    @Excel(name = "LDD编号")
    private String lddNumbers;

    @Excel(name = "模具类别")
    private String mouldCategory;

    @Excel(name = "模具ID")
    private String mouldId;

    @Excel(name = "模具图片的URL")
    private String pictureUrl;

    @Excel(name = "模具的颜色描述")
    private String color;

    @Excel(name = "模具生产的产品名称")
    private String productName;

    @Excel(name = "模具数量")
    private Integer quantity;

    @Excel(name = "模具用料")
    private String material;

//    @Excel(name = "设计是否已确认", readConverterExp = "1=已确认,0=未确认")
//    private Long confirm;

    // 构造函数：将 Set 转换为逗号分隔字符串
    public DesignPatternsExportDTO(DesignPatternsResult result) {
        this.id = result.getId();
        this.mouldNumbers = String.join(",", result.getMouldNumber());
        this.lddNumbers = String.join(",", result.getLddNumber());
        this.mouldCategory = String.join(",", result.getMouldCategory());
        this.mouldId = String.join(",", result.getMouldId());
        this.pictureUrl = String.join(",", result.getPictureUrl());
        this.color = String.join(",", result.getColor());
        this.productName = String.join(",", result.getProductName());
        this.quantity = result.getQuantity();
        this.material = String.join(",", result.getMaterial());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMouldNumbers() {
        return mouldNumbers;
    }

    public void setMouldNumbers(String mouldNumbers) {
        this.mouldNumbers = mouldNumbers;
    }

    public String getLddNumbers() {
        return lddNumbers;
    }

    public void setLddNumbers(String lddNumbers) {
        this.lddNumbers = lddNumbers;
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

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

}
