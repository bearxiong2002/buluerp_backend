package com.ruoyi.web.request.material;

import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.validation.Save;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AddPurchasedMaterialRequest {
    @ApiModelProperty("模具编号")
    @Excel(name = "模具编号")
    @Example("ABC123")
    @NotBlank(message = "模具编号不能为空", groups = Save.class)
    private String mouldNumber;

    @ApiModelProperty("规格名称")
    @Excel(name = "规格名称")
    @Example("A1")
    private String specificationName;

    @ApiModelProperty("料型")
    @Excel(name = "料型")
    @Example("A1")
    @NotNull(message = "料型不能为空", groups = Save.class)
    private String materialType;

    @ApiModelProperty("单重")
    @Excel(name = "单重")
    @Example("100")
    private Double singleWeight;

    @ApiModelProperty("外购编码")
    @Excel(name = "外购编码")
    @Example("A1")
    @NotBlank(message = "外购编码不能为空", groups = Save.class)
    private String purchaseCode;

    @ApiModelProperty("外购图片")
    @Excel(name = "外购图片", cellType = Excel.ColumnType.IMAGE)
    private String pictureUrl;

    @ApiModelProperty("单价")
    @Excel(name = "单价")
    @Example("100.00")
    private BigDecimal unitPrice;

    @ApiModelProperty("供应商")
    @Excel(name = "供应商")
    @Example("凝聚")
    private String supplier;

    @ApiModelProperty("产品编码")
    @Excel(name = "产品编码")
    @Example("ABC123")
    private String productCode;

    @ApiModelProperty("外购图片文件")
    private MultipartFile pictureFile;

    public String getMouldNumber() {
        return mouldNumber;
    }

    public void setMouldNumber(String mouldNumber) {
        this.mouldNumber = mouldNumber;
    }

    public String getSpecificationName() {
        return specificationName;
    }

    public void setSpecificationName(String specificationName) {
        this.specificationName = specificationName;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public Double getSingleWeight() {
        return singleWeight;
    }

    public void setSingleWeight(Double singleWeight) {
        this.singleWeight = singleWeight;
    }

    public String getPurchaseCode() {
        return purchaseCode;
    }

    public void setPurchaseCode(String purchaseCode) {
        this.purchaseCode = purchaseCode;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public MultipartFile getPictureFile() {
        return pictureFile;
    }

    public void setPictureFile(MultipartFile pictureFile) {
        this.pictureFile = pictureFile;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
}
