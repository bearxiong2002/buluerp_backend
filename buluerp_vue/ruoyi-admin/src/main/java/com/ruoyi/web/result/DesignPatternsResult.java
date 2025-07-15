package com.ruoyi.web.result;

import com.ruoyi.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Set;

public class DesignPatternsResult {

    /** 主设记编号 */
    @Excel(name = "产品内部id")
    private String id;

    /** 模具编号，用于唯一标识模具 */
    @Excel(name = "模具编号")
    private Set<String>  mouldNumber;

    /** LDD编号，与模具相关的编号 */
    @Excel(name = "LDD编号")
    private Set<String> lddNumber;

    /** 模具类别，如注塑模具、冲压模具等 */
    @Excel(name = "模具类别")
    private Set<String> mouldCategory;

    /** 模具ID，用于内部标识模具 */
    @Excel(name = "模具ID")
    private Set<Long> materialId;

    /** 模具图片的URL链接，用于存储模具外观图片 */
    @Excel(name = "模具图片",cellType = Excel.ColumnType.IMAGE, height = 80)
    private Set<String> pictureUrl;

    /** 模具的颜色描述 */
    @Excel(name = "模具的颜色描述")
    private Set<String> color;

    /** 模具生产的产品名称 */
    @Excel(name = "模具生产的产品名称")
    private Set<String> productName;

    /** 模具的数量 */
    @Excel(name = "模具数量")
    private Integer quantity;

    /** 模具的用料，如钢材、铝合金等 */
    @Excel(name = "模具用料")
    private Set<String> material;

    @Excel(name = "设计是否已确认", readConverterExp = "1=已确认,0=未确认")
    private Long confirm;

    public Long getConfirm() {
        return confirm;
    }

    public void setConfirm(Long confirm) {
        this.confirm = confirm;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String  getId()
    {
        return id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Set<String> getMouldNumber() {
        return mouldNumber;
    }

    public void setMouldNumber(Set<String> mouldNumber) {
        this.mouldNumber = mouldNumber;
    }

    public Set<String> getLddNumber() {
        return lddNumber;
    }

    public void setLddNumber(Set<String> lddNumber) {
        this.lddNumber = lddNumber;
    }

    public Set<String> getMouldCategory() {
        return mouldCategory;
    }

    public void setMouldCategory(Set<String> mouldCategory) {
        this.mouldCategory = mouldCategory;
    }

    public Set<Long> getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Set<Long> materialId) {
        this.materialId = materialId;
    }

    public Set<String> getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(Set<String> pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Set<String> getColor() {
        return color;
    }

    public void setColor(Set<String> color) {
        this.color = color;
    }

    public Set<String> getProductName() {
        return productName;
    }

    public void setProductName(Set<String> productName) {
        this.productName = productName;
    }

    public Set<String> getMaterial() {
        return material;
    }

    public void setMaterial(Set<String> material) {
        this.material = material;
    }

    public DesignPatternsResult(String id, Set<String> mouldNumber, Set<String> lddNumber, Set<String> mouldCategory, Set<Long> materialId, Set<String> pictureUrl, Set<String> color, Set<String> productName, Integer quantity, Set<String> material, Long confirm) {
        this.id = id;
        this.mouldNumber = mouldNumber;
        this.lddNumber = lddNumber;
        this.mouldCategory = mouldCategory;
        this.materialId = materialId;
        this.pictureUrl = pictureUrl;
        this.color = color;
        this.productName = productName;
        this.quantity = quantity;
        this.material = material;
        this.confirm=confirm;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mouldNumber", getMouldNumber())
                .append("lddNumber", getLddNumber())
                .append("mouldCategory", getMouldCategory())
                .append("materialId", getMaterialId())
                .append("pictureUrl", getPictureUrl())
                .append("color", getColor())
                .append("productName", getProductName())
                .append("quantity", getQuantity())
                .append("material", getMaterial())
                .toString();
    }
}
