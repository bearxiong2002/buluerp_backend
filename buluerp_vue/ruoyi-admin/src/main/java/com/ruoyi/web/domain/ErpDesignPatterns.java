package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 erp_design_patterns
 * 
 * @author ruoyi
 * @date 2025-04-09
 */
@TableName(value = "erp_design_patterns")
public class ErpDesignPatterns extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long productId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long createUserId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long orderId;

    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long confirm;

    public Long getConfirm() {
        return confirm;
    }

    public void setConfirm(Long confirm) {
        this.confirm = confirm;
    }

    public ErpDesignPatterns(Long productId, Long createUserId, Long orderId) {
        this.productId = productId;
        this.createUserId = createUserId;
        this.orderId = orderId;
    }

    public ErpDesignPatterns(Long productId, Long createUserId, Long orderId, Long confirm) {
        this.productId = productId;
        this.createUserId = createUserId;
        this.orderId = orderId;
        this.confirm = confirm;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setProductId(Long productId) 
    {
        this.productId = productId;
    }

    public Long getProductId() 
    {
        return productId;
    }

    public void setCreateUserId(Long createUserId) 
    {
        this.createUserId = createUserId;
    }

    public Long getCreateUserId() 
    {
        return createUserId;
    }

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("productId", getProductId())
            .append("createUserId", getCreateUserId())
            .append("orderId", getOrderId())
            .append("createTime", getCreateTime())
            .toString();
    }
}
