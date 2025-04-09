package com.ruoyi.web.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

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

    /** 操作人ID（外键引用用户表） */
    @Excel(name = "操作人ID", readConverterExp = "外=键引用用户表")
    private Long operatorId;

    /** 数量 */
    @Excel(name = "数量")
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
    @Excel(name = "客户ID", readConverterExp = "外=键引用客户表")
    private Long customerId;

    /** 产品ID（外键引用产品表） */
    @Excel(name = "产品ID", readConverterExp = "外=键引用产品表")
    private Long productId;

    /** 布产ID（外键引用布产表） */
    @Excel(name = "布产ID", readConverterExp = "外=键引用布产表")
    private Long productionId;

    /** 外购ID（外键引用采购表） */
    @Excel(name = "外购ID", readConverterExp = "外=键引用采购表")
    private Long purchaseId;

    /** 分包ID（外键引用分包商表） */
    @Excel(name = "分包ID", readConverterExp = "外=键引用分包商表")
    private Long subcontractId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setOperatorId(Long operatorId) 
    {
        this.operatorId = operatorId;
    }

    public Long getOperatorId() 
    {
        return operatorId;
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
            .append("operatorId", getOperatorId())
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
}
