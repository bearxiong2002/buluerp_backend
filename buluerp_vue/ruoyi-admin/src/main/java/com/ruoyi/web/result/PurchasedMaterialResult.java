package com.ruoyi.web.result;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.web.domain.ErpMaterialInfo;
import com.ruoyi.web.domain.ErpPurchaseInfo;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

public class PurchasedMaterialResult extends ErpMaterialInfo {
    @ApiModelProperty("外购编码")
    @Excel(name = "外购编码")
    private String purchaseCode;

    @ApiModelProperty("外购图片链接")
    @Excel(name = "外购图片", cellType = Excel.ColumnType.IMAGE)
    private String pictureUrl;

    @ApiModelProperty("单价")
    @Excel(name = "单价")
    private BigDecimal unitPrice;

    @ApiModelProperty("供应商")
    @Excel(name = "供应商")
    private String supplier;

    @ApiModelProperty("是否外购")
    @Excel(name = "是否外购", readConverterExp = "true=是,false=否")
    private Boolean isPurchased;

    public PurchasedMaterialResult flatten() {
        ErpPurchaseInfo pi = this.getPurchaseInfo();
        if (pi != null) {
            setPurchaseCode(pi.getPurchaseCode());
            setPictureUrl(pi.getPictureUrl());
            setUnitPrice(pi.getUnitPrice());
            setSupplier(pi.getSupplier());
            setPurchased(true);
        }
        return this;
    }

    public static PurchasedMaterialResult fromMaterialInfo(ErpMaterialInfo materialInfo) {
        PurchasedMaterialResult result = new PurchasedMaterialResult();
        BeanUtils.copyProperties(materialInfo, result);
        result.setPurchased(false);
        return result.flatten();
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getPurchaseCode() {
        return purchaseCode;
    }

    public void setPurchaseCode(String purchaseCode) {
        this.purchaseCode = purchaseCode;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Boolean getPurchased() {
        return isPurchased;
    }

    @Override
    public void setPurchased(Boolean purchased) {
        isPurchased = purchased;
    }
}
