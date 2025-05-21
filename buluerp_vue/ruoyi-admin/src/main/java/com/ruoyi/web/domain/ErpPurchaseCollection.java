package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

public class ErpPurchaseCollection extends BaseEntity {
    @TableId(type = IdType.AUTO)
    @Excel(name = "序号")
    private Integer id;

    @Excel(name = "订单编号")
    private String orderCode;

    @Excel(name = "创建时间")
    private Date creationTime;

    @Excel(name = "操作人")
    private String operator;

    @Excel(name = "产品编号")
    private Long productId;

    @Excel(name = "下单时间")
    private Date orderTime;

    @Excel(name = "图片链接")
    private String pictureUrl;

    @Excel(name = "外购编码")
    private String purchaseCode;

    @Excel(name = "外购模具编号")
    private String mouldNumber;

    @Excel(name = "规格")
    private String specification;

    @Excel(name = "采购数量")
    private Integer purchaseQuantity;

    @Excel(name = "颜色编号")
    private String colorCode;

    @Excel(name = "料别")
    private String materialType;

    @Excel(name = "单重")
    private Double singleWeight;

    @Excel(name = "采购重量")
    private Double purchaseWeight;

    @Excel(name = "货期，供应商承诺的交货时间范围")
    private String deliveryTime;

    @Excel(name = "交货日期，实际交货日期")
    private Date deliveryDate;

    @Excel(name = "供应商")
    private String supplier;

    @Excel(name = "备注")
    private String remarks;

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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
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

    public String getMouldNumber() {
        return mouldNumber;
    }

    public void setMouldNumber(String mouldNumber) {
        this.mouldNumber = mouldNumber;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public Integer getPurchaseQuantity() {
        return purchaseQuantity;
    }

    public void setPurchaseQuantity(Integer purchaseQuantity) {
        this.purchaseQuantity = purchaseQuantity;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
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

    public Double getPurchaseWeight() {
        return purchaseWeight;
    }

    public void setPurchaseWeight(Double purchaseWeight) {
        this.purchaseWeight = purchaseWeight;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
