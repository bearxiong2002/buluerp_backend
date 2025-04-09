package com.ruoyi.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 外购资料，用于存储外购物料的基本信息和相关数据对象 erp_purchase_info
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
public class ErpPurchaseInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 外购编码，唯一标识外购物料 */
    private String purchaseCode;

    /** 图片链接，用于存储外购物料的图片 */
    @Excel(name = "图片链接，用于存储外购物料的图片")
    private String pictureUrl;

    /** 外购模具编号，与模具相关联的编号 */
    @Excel(name = "外购模具编号，与模具相关联的编号")
    private String mouldNumber;

    /** 单重，单位为克，表示单个物料的重量 */
    @Excel(name = "单重，单位为克，表示单个物料的重量")
    private BigDecimal singleWeight;

    /** 单价，单位为货币单位，表示物料的单价 */
    @Excel(name = "单价，单位为货币单位，表示物料的单价")
    private BigDecimal unitPrice;

    /** 材料，用于描述外购物料的材料类型 */
    @Excel(name = "材料，用于描述外购物料的材料类型")
    private String material;

    /** 供应商，提供外购物料的供应商名称 */
    @Excel(name = "供应商，提供外购物料的供应商名称")
    private String supplier;

    /** 规格名称，外购物料的具体规格描述 */
    @Excel(name = "规格名称，外购物料的具体规格描述")
    private String specificationName;

    public void setPurchaseCode(String purchaseCode) 
    {
        this.purchaseCode = purchaseCode;
    }

    public String getPurchaseCode() 
    {
        return purchaseCode;
    }

    public void setPictureUrl(String pictureUrl) 
    {
        this.pictureUrl = pictureUrl;
    }

    public String getPictureUrl() 
    {
        return pictureUrl;
    }

    public void setMouldNumber(String mouldNumber) 
    {
        this.mouldNumber = mouldNumber;
    }

    public String getMouldNumber() 
    {
        return mouldNumber;
    }

    public void setSingleWeight(BigDecimal singleWeight) 
    {
        this.singleWeight = singleWeight;
    }

    public BigDecimal getSingleWeight() 
    {
        return singleWeight;
    }

    public void setUnitPrice(BigDecimal unitPrice) 
    {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getUnitPrice() 
    {
        return unitPrice;
    }

    public void setMaterial(String material) 
    {
        this.material = material;
    }

    public String getMaterial() 
    {
        return material;
    }

    public void setSupplier(String supplier) 
    {
        this.supplier = supplier;
    }

    public String getSupplier() 
    {
        return supplier;
    }

    public void setSpecificationName(String specificationName) 
    {
        this.specificationName = specificationName;
    }

    public String getSpecificationName() 
    {
        return specificationName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("purchaseCode", getPurchaseCode())
            .append("pictureUrl", getPictureUrl())
            .append("mouldNumber", getMouldNumber())
            .append("singleWeight", getSingleWeight())
            .append("unitPrice", getUnitPrice())
            .append("material", getMaterial())
            .append("supplier", getSupplier())
            .append("specificationName", getSpecificationName())
            .toString();
    }
}
