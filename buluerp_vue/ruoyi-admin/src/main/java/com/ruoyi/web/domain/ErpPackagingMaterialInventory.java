package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

import java.time.LocalDateTime;
import java.util.Date;

@TableName("erp_packaging_material_inventory")
public class ErpPackagingMaterialInventory {

    @Excel(name = "记录ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Excel(name = "订单编号")
    @TableField("order_code")
    private String orderCode;

    @Excel(name = "产品货号")
    @TableField("product_part_number")
    private String productPartNumber;

    @Excel(name = "分包编号")
    @TableField("packing_number")
    private String packingNumber;

    @Excel(name = "入库数量")
    @TableField("in_quantity")
    private Integer inQuantity;

    @Excel(name = "出库数量")
    @TableField("out_quantity")
    private Integer outQuantity;

    @Excel(name = "当前总库存")
    private Integer totalQuantity;

    @Excel(name = "更新时间",dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    public void total(){
        this.totalQuantity=this.inQuantity+this.outQuantity;
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

    public String getProductPartNumber() {
        return productPartNumber;
    }

    public void setProductPartNumber(String productPartNumber) {
        this.productPartNumber = productPartNumber;
    }

    public String getPackingNumber() {
        return packingNumber;
    }

    public void setPackingNumber(String packingNumber) {
        this.packingNumber = packingNumber;
    }

    public Integer getInQuantity() {
        return inQuantity;
    }

    public void setInQuantity(Integer inQuantity) {
        this.inQuantity = inQuantity;
    }

    public Integer getOutQuantity() {
        return outQuantity;
    }

    public void setOutQuantity(Integer outQuantity) {
        this.outQuantity = outQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}