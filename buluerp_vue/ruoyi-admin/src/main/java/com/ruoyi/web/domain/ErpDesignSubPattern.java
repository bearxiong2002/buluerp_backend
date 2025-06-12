package com.ruoyi.web.domain;

import io.swagger.annotations.ApiModel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 模具信息，用于存储模具的基本信息和相关数据对象 erp_design_sub_pattern
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@ApiModel(value = "设计子模式")
public class ErpDesignSubPattern extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键，唯一标识每条记录 */
    private Long id;

    /** 主设记编号 */
    @Excel(name = "主设计编号")
    private Long designPatternId;

    /** 模具编号，用于唯一标识模具 */
    @Excel(name = "模具编号，用于唯一标识模具")
    private String mouldNumber;

    /** LDD编号，与模具相关的编号 */
    @Excel(name = "LDD编号，与模具相关的编号")
    private String lddNumber;

    /** 模具类别，如注塑模具、冲压模具等 */
    @Excel(name = "模具类别，如注塑模具、冲压模具等")
    private String mouldCategory;

    /** 模具ID，用于内部标识模具 */
    @Excel(name = "模具ID，用于内部标识模具")
    private String mouldId;

    /** 模具图片的URL链接，用于存储模具外观图片 */
    @Excel(name = "模具图片的URL链接，用于存储模具外观图片")
    private String pictureUrl;

    /** 模具的颜色描述 */
    @Excel(name = "模具的颜色描述")
    private String color;

    /** 模具生产的产品名称 */
    @Excel(name = "模具生产的产品名称")
    private String productName;

    /** 模具的数量 */
    @Excel(name = "模具的数量")
    private Long quantity;

    /** 模具的用料，如钢材、铝合金等 */
    @Excel(name = "模具的用料，如钢材、铝合金等")
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("designPatternId", getDesignPatternId())
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
