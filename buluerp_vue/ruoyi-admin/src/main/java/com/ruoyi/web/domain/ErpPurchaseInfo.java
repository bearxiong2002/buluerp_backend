package com.ruoyi.web.domain;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.validation.NullOrNotBlank;
import com.ruoyi.common.validation.Save;
import com.ruoyi.common.validation.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 外购资料，用于存储外购物料的基本信息和相关数据对象 erp_purchase_info
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
@TableName("erp_purchase_info")
@ApiModel("外购资料")
public class ErpPurchaseInfo
{
    @TableId(type = IdType.AUTO)
    @TableField(condition = SqlCondition.LIKE)
    @ApiModelProperty(value = "序号 [GET|list|PUT|DELETE|response]")
    private Long id;

    /** 外购编码，唯一标识外购物料 */
    @TableField(condition = SqlCondition.LIKE)
    @Excel(name = "外购编码")
    @Example("ABC123")
    @NotBlank(groups = {Save.class}, message = "外购编码不能为空白")
    @NullOrNotBlank(groups = {Update.class}, message = "外购编码不能为空白")
    @ApiModelProperty(value = "外购编码，唯一标识外购物料 [list|POST|PUT|response]", required = true)
    private String purchaseCode;

    /** 图片链接，用于存储外购物料的图片 */
    @TableField(condition = SqlCondition.LIKE)
    @Excel(name = "图片", cellType = Excel.ColumnType.IMAGE, height = 100)
    @Example
    @ApiModelProperty(value = "图片链接，填空字符串且不上传文件可删除图片 [list|POST|PUT|response]")
    private String pictureUrl;

    /** 单价，单位为货币单位，表示物料的单价 */
    @Excel(name = "单价")
    @Example("123.456")
    @NotNull(groups = {Save.class}, message = "单价不能为空")
    @Range(min = 0, message = "单价不能小于0", groups = {Save.class, Update.class})
    @ApiModelProperty(value = "单价，单位为货币单位，表示物料的单价 [list|POST|PUT|response]", required = true)
    private BigDecimal unitPrice;

    /** 材料，用于描述外购物料的材料类型 */
    @TableField(condition = SqlCondition.LIKE)
    @Excel(name = "物料ID")
    @Example("1")
    @NotNull(groups = {Save.class}, message = "请填写对应物料")
    @ApiModelProperty(value = "物料ID，关联的物料资料ID [list|POST|PUT|response]", required = true)
    private Long materialId;

    /** 供应商，提供外购物料的供应商名称 */
    @TableField(condition = SqlCondition.LIKE)
    @Excel(name = "供应商")
    @Example("凝聚")
    @NotBlank(groups = {Save.class}, message = "供应商不能为空白")
    @NullOrNotBlank(groups = {Update.class}, message = "供应商不能为空白")
    @ApiModelProperty(value = "供应商，提供外购物料的供应商名称 [list|POST|PUT|response]", required = true)
    private String supplier;

    @JsonIgnore
    @TableField(exist = false)
    @ApiModelProperty(value = "图片文件，用于上传图片文件 [POST|PUT]")
    private MultipartFile picture;

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

    public void setUnitPrice(BigDecimal unitPrice) 
    {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getUnitPrice() 
    {
        return unitPrice;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setSupplier(String supplier) 
    {
        this.supplier = supplier;
    }

    public String getSupplier() 
    {
        return supplier;
    }

    public MultipartFile getPicture() {
        return picture;
    }

    public void setPicture(MultipartFile picture) {
        this.picture = picture;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
