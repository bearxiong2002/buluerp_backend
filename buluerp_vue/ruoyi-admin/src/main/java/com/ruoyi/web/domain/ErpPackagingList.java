package com.ruoyi.web.domain;

import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

public class ErpPackagingList extends BaseEntity {
    private Integer id;
    private String orderCode;
    private Date creationTime;
    private String operator;
    private String productNumber;
    private String productNameCn;
    private String packagingListNumber;
    private Date releaseDate;
    private String accessoryType;
    private Integer accessoryTotal;
    private Boolean isManual;
    private Boolean isMinifigure;
    private Boolean isTool;
    private String productionLine;
    private Double bagWeight;
    private String bagSpecification;
    private String packageAccessories;
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
