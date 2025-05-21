package com.ruoyi.web.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

public class ErpPackagingList extends BaseEntity {
    @Excel(name = "序号")
    private Integer id;

    @Excel(name = "订单号")
    private String orderCode;

    @Excel(name = "创建时间")
    private Date creationTime;

    @Excel(name = "操作人")
    private String operator;

    @Excel(name = "产品编号")
    private String productNumber;

    @Excel(name = "产品中文名称")
    private String productNameCn;

    @Excel(name = "分包表编号")
    private String packagingListNumber;

    @Excel(name = "发布日期")
    private Date releaseDate;

    @Excel(name = "配件种类")
    private String accessoryType;

    @Excel(name = "配件总数")
    private Integer accessoryTotal;

    @Excel(name = "是否是说明书", readConverterExp = "true=是,false=否")
    private Boolean isManual;

    @Excel(name = "是否是人仔", readConverterExp = "true=是,false=否")
    private Boolean isMinifigure;

    @Excel(name = "是否是起件器", readConverterExp = "true=是,false=否")
    private Boolean isTool;

    @Excel(name = "生产线")
    private String productionLine;

    @Excel(name = "本袋重量")
    private Double bagWeight;

    @Excel(name = "本袋规格")
    private String bagSpecification;

    @Excel(name = "本包配件")
    private String packageAccessories;

    @Excel(name = "本包数量")
    private Integer packageQuantity;

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getProductNameCn() {
        return productNameCn;
    }

    public void setProductNameCn(String productNameCn) {
        this.productNameCn = productNameCn;
    }

    public String getPackagingListNumber() {
        return packagingListNumber;
    }

    public void setPackagingListNumber(String packagingListNumber) {
        this.packagingListNumber = packagingListNumber;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getAccessoryType() {
        return accessoryType;
    }

    public void setAccessoryType(String accessoryType) {
        this.accessoryType = accessoryType;
    }

    public Integer getAccessoryTotal() {
        return accessoryTotal;
    }

    public void setAccessoryTotal(Integer accessoryTotal) {
        this.accessoryTotal = accessoryTotal;
    }

    public Boolean getManual() {
        return isManual;
    }

    public void setManual(Boolean manual) {
        isManual = manual;
    }

    public Boolean getMinifigure() {
        return isMinifigure;
    }

    public void setMinifigure(Boolean minifigure) {
        isMinifigure = minifigure;
    }

    public Boolean getTool() {
        return isTool;
    }

    public void setTool(Boolean tool) {
        isTool = tool;
    }

    public String getProductionLine() {
        return productionLine;
    }

    public void setProductionLine(String productionLine) {
        this.productionLine = productionLine;
    }

    public Double getBagWeight() {
        return bagWeight;
    }

    public void setBagWeight(Double bagWeight) {
        this.bagWeight = bagWeight;
    }

    public String getBagSpecification() {
        return bagSpecification;
    }

    public void setBagSpecification(String bagSpecification) {
        this.bagSpecification = bagSpecification;
    }

    public String getPackageAccessories() {
        return packageAccessories;
    }

    public void setPackageAccessories(String packageAccessories) {
        this.packageAccessories = packageAccessories;
    }

    public Integer getPackageQuantity() {
        return packageQuantity;
    }

    public void setPackageQuantity(Integer packageQuantity) {
        this.packageQuantity = packageQuantity;
    }

}
