package com.ruoyi.web.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 设计造型对象 erp_design_style
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
public class ErpDesignStyle extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键，唯一标识每条记录 */
    private Long id;

    /** 主设记编号 */
    @Excel(name = "主设记编号")
    private Long designPatternId;

    /** 不同造型表的分组表示 */
    @Excel(name = "造型表的分组")
    private Long groupId;

    /** 模具编号，用于唯一标识模具 */
    @Excel(name = "模具编号")
    private String mouldNumber;

    /** LDD编号，与模具相关的编号 */
    @Excel(name = "LDD编号")
    private String lddNumber;

    /** 模具类别，如注塑模具、冲压模具等 */
    @Excel(name = "模具类别")
    private String mouldCategory;

    /** 模具ID，用于内部标识模具 */
    @Excel(name = "模具ID")
    private String mouldId;

    /** 模具图片的URL链接，用于存储模具外观图片 */
    @Excel(name = "模具图片的URL")
    private String pictureUrl;

    /** 模具的颜色描述 */
    @Excel(name = "模具的颜色描述")
    private String color;

    /** 模具生产的产品名称 */
    @Excel(name = "模具生产的产品名称")
    private String productName;

    /** 模具的数量 */
    @Excel(name = "模具数量")
    private Long quantity;

    /** 模具的用料，如钢材、铝合金等 */
    @Excel(name = "模具用料")
    private String material;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setDesignPatternId(Long designPatternId) 
    {
        this.designPatternId = designPatternId;
    }

    public Long getDesignPatternId() 
    {
        return designPatternId;
    }

    public void setGroupId(Long groupId) 
    {
        this.groupId = groupId;
    }

    public Long getGroupId() 
    {
        return groupId;
    }

    public void setMouldNumber(String mouldNumber) 
    {
        this.mouldNumber = mouldNumber;
    }

    public String getMouldNumber() 
    {
        return mouldNumber;
    }

    public void setLddNumber(String lddNumber) 
    {
        this.lddNumber = lddNumber;
    }

    public String getLddNumber() 
    {
        return lddNumber;
    }

    public void setMouldCategory(String mouldCategory) 
    {
        this.mouldCategory = mouldCategory;
    }

    public String getMouldCategory() 
    {
        return mouldCategory;
    }

    public void setMouldId(String mouldId) 
    {
        this.mouldId = mouldId;
    }

    public String getMouldId() 
    {
        return mouldId;
    }

    public void setPictureUrl(String pictureUrl) 
    {
        this.pictureUrl = pictureUrl;
    }

    public String getPictureUrl() 
    {
        return pictureUrl;
    }

    public void setColor(String color) 
    {
        this.color = color;
    }

    public String getColor() 
    {
        return color;
    }

    public void setProductName(String productName) 
    {
        this.productName = productName;
    }

    public String getProductName() 
    {
        return productName;
    }

    public void setQuantity(Long quantity) 
    {
        this.quantity = quantity;
    }

    public Long getQuantity() 
    {
        return quantity;
    }

    public void setMaterial(String material) 
    {
        this.material = material;
    }

    public String getMaterial() 
    {
        return material;
    }

    public ErpDesignStyle(Long designPatternId,Long groupId, String mouldNumber, String lddNumber, String mouldCategory, String mouldId, String pictureUrl, String color, String productName, Long quantity, String material) {
        this.designPatternId = designPatternId;
        this.groupId= groupId;
        this.mouldNumber = mouldNumber;
        this.lddNumber = lddNumber;
        this.mouldCategory = mouldCategory;
        this.mouldId = mouldId;
        this.pictureUrl = pictureUrl;
        this.color = color;
        this.productName = productName;
        this.quantity = quantity;
        this.material = material;
    }

    public ErpDesignStyle(Long id, Long designPatternId, Long groupId, String mouldNumber, String lddNumber, String mouldCategory, String mouldId, String pictureUrl, String color, String productName, Long quantity, String material) {
        this.id = id;
        this.designPatternId = designPatternId;
        this.groupId = groupId;
        this.mouldNumber = mouldNumber;
        this.lddNumber = lddNumber;
        this.mouldCategory = mouldCategory;
        this.mouldId = mouldId;
        this.pictureUrl = pictureUrl;
        this.color = color;
        this.productName = productName;
        this.quantity = quantity;
        this.material = material;
    }

    public ErpDesignStyle(Long id,Long designPatternId, Long groupId) {
        this.id=id;
        this.designPatternId = designPatternId;
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("designPatternId", getDesignPatternId())
            .append("groupId", getGroupId())
            .append("mouldNumber", getMouldNumber())
            .append("lddNumber", getLddNumber())
            .append("mouldCategory", getMouldCategory())
            .append("mouldId", getMouldId())
            .append("pictureUrl", getPictureUrl())
            .append("color", getColor())
            .append("productName", getProductName())
            .append("quantity", getQuantity())
            .append("material", getMaterial())
            .toString();
    }
}
