package com.ruoyi.web.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.validation.Save;
import com.ruoyi.common.validation.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@ApiModel("采购计划")
public class ErpPurchaseCollection extends BaseEntity {
    @Excel(name = "序号")
    private Long id;

    @Excel(name = "订单编号")
    @Example("BLK20250528000001")
    private String orderCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", type = Excel.Type.EXPORT, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date creationTime;

    @Excel(name = "操作人", type = Excel.Type.EXPORT)
    private String operator;

    @Excel(name = "产品编号")
    @Example("1")
    private Long productId;

    @Excel(name = "下单时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Example("2022-05-28 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date orderTime;

    @Excel(name = "图片链接", cellType = Excel.ColumnType.IMAGE)
    private String pictureUrl;

    @Excel(name = "外购编码")
    @Example("ABC123")
    private String purchaseCode;

    @Excel(name = "外购模具编号")
    @Example("ABC123")
    private String mouldNumber;

    @Excel(name = "规格")
    @Example("ABC")
    private String specification;

    @Excel(name = "采购数量")
    @Range(min = 1, message = "采购数量不能小于1", groups = {Save.class, Update.class})
    @Example("1")
    private Long purchaseQuantity;

    @Excel(name = "颜色编号")
    @Example("123,456,789")
    private String colorCode;

    @Excel(name = "料别")
    @Example("YL123456")
    private String materialType;

    @Excel(name = "单重")
    @Range(min = 0, message = "单重不能小于等于0", groups = {Save.class, Update.class})
    @Example("1.23")
    private Double singleWeight;

    @Excel(name = "采购重量")
    @Range(min = 0, message = "采购重量不能小于等于0", groups = {Save.class, Update.class})
    @Example("1.23")
    private Double purchaseWeight;

    @Excel(name = "货期，供应商承诺的交货时间范围")
    @Example("2022-05-28 10:00:00~2022-05-28 10:00:00")
    private String deliveryTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "交货日期，实际交货日期", dateFormat = "yyyy-MM-dd")
    @Example("2022-05-28")
    private Date deliveryDate;

    @Excel(name = "供应商")
    @Example("凝聚")
    private String supplier;

    @Excel(name = "备注")
    @Example("无")
    private String remarks;

    @Excel(name = "外购资料ID")
    @Example("1")
    private Long purchaseId;

    @JsonIgnore
    private MultipartFile picture;

    List<Long> materialIds;

    @ApiModelProperty(value = "状态 0=待审核，1=审核通过")
    private Long status;

    @ApiModelProperty(value = "审核状态 0=待审核，1=审核中，-1=审核被拒绝，2=审核通过")
    private Integer auditStatus;

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public List<Long> getMaterialIds() {
        return materialIds;
    }

    public void setMaterialIds(List<Long> materialIds) {
        this.materialIds = materialIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getPurchaseQuantity() {
        return purchaseQuantity;
    }

    public void setPurchaseQuantity(Long purchaseQuantity) {
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

    public MultipartFile getPicture() {
        return picture;
    }

    public void setPicture(MultipartFile picture) {
        this.picture = picture;
    }

    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }
}
