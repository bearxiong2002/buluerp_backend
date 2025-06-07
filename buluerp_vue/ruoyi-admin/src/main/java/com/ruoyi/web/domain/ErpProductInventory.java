package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

import java.time.LocalDateTime;

@TableName("erp_product_inventory")
public class ErpProductInventory {

    @Excel(name = "记录ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Excel(name = "产品货号")
    @TableField("product_part_number")
    private String productPartNumber;

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
        int inQty = (this.inQuantity != null) ? this.inQuantity : 0;
        int outQty = (this.outQuantity != null) ? this.outQuantity : 0;
        this.totalQuantity = inQty + outQty;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductPartNumber() {
        return productPartNumber;
    }

    public void setProductPartNumber(String productPartNumber) {
        this.productPartNumber = productPartNumber;
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

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}