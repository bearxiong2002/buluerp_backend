package com.ruoyi.web.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.ruoyi.common.domain.validation.Save;
import com.ruoyi.common.domain.validation.Update;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.hibernate.validator.constraints.Range;

/**
 * 订单对象 erp_orders
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
public class ErpOrders extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    @Excel(name = "内部ID")
    private String innerId;

    @Excel(name = "外部ID")
    private String outerId;

    /** 操作人ID（外键引用用户表） */
    @Excel(name = "操作人姓名")
    private String operator;

    @Excel(name = "客户姓名")
    private String customerName;

    /** 数量 */
    @Excel(name = "数量")
    @Range(min = 1, message = "数量不能小于1", groups = {Save.class, Update.class})
    private Long quantity;

    /** 交货期限 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "交货期限", width = 30, dateFormat = "yyyy-MM-dd")
    private Date deliveryDeadline;

    /** 交货时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "交货时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date deliveryTime;

    /** 状态（0:创建 1:已发货 2:已完成等） */
    @Excel(name = "状态", readConverterExp = "0=:创建,1=:已发货,2=:已完成等")
    private Integer status;

    /** 客户ID（外键引用客户表） */
    @Excel(name = "客户ID")
    private Long customerId;

    /** 产品ID（外键引用产品表） */
    @Excel(name = "产品ID")
    private Long productId;

    /** 布产ID（外键引用布产表） */
    @Excel(name = "布产ID")
    private Long productionId;

    /** 外购ID（外键引用采购表） */
    @Excel(name = "外购ID")
    private Long purchaseId;

    /** 分包ID（外键引用分包商表） */
    @Excel(name = "分包ID")
    private Long subcontractId;

    @Excel(name = "其它基本信息")
    private String remark;

    @JsonUnwrapped(prefix = "customer-")
    private ErpCustomers customer;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setQuantity(Long quantity) 
    {
        this.quantity = quantity;
    }

    public Long getQuantity() 
    {
        return quantity;
    }

    public void setDeliveryDeadline(Date deliveryDeadline) 
    {
        this.deliveryDeadline = deliveryDeadline;
    }

    public Date getDeliveryDeadline() 
    {
        return deliveryDeadline;
    }

    public void setDeliveryTime(Date deliveryTime) 
    {
        this.deliveryTime = deliveryTime;
    }

    public Date getDeliveryTime() 
    {
        return deliveryTime;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    public void setCustomerId(Long customerId) 
    {
        this.customerId = customerId;
    }

    public Long getCustomerId() 
    {
        return customerId;
    }

    public void setProductId(Long productId) 
    {
        this.productId = productId;
    }

    public Long getProductId() 
    {
        return productId;
    }

    public void setProductionId(Long productionId) 
    {
        this.productionId = productionId;
    }

    public Long getProductionId() 
    {
        return productionId;
    }

    public void setPurchaseId(Long purchaseId) 
    {
        this.purchaseId = purchaseId;
    }

    public Long getPurchaseId() 
    {
        return purchaseId;
    }

    public void setSubcontractId(Long subcontractId) 
    {
        this.subcontractId = subcontractId;
    }

    public Long getSubcontractId() 
    {
        return subcontractId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("operator", getOperator())
            .append("quantity", getQuantity())
            .append("deliveryDeadline", getDeliveryDeadline())
            .append("deliveryTime", getDeliveryTime())
            .append("status", getStatus())
            .append("customerId", getCustomerId())
            .append("productId", getProductId())
            .append("productionId", getProductionId())
            .append("purchaseId", getPurchaseId())
            .append("subcontractId", getSubcontractId())
            .append("updateTime", getUpdateTime())
            .toString();
    }

    public String generateInnerId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(getCreateTime());
        return "BLK"
                + date
                + String.format("%06d", getId());
    }

    public String generateOuterId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(getCreateTime());
        return "OUT"
                + date
                + String.format("%06d", getId());
    }

    public String getInnerId() {
        return innerId;
    }

    public void setInnerId(String innerId) {
        this.innerId = innerId;
    }

    public String getOuterId() {
        return outerId;
    }

    public void setOuterId(String outerId) {
        this.outerId = outerId;
    }

    public ErpCustomers getCustomer() {
        return customer;
    }

    public void setCustomer(ErpCustomers customer) {
        this.customer = customer;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
