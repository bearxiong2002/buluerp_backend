package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

import java.time.LocalDateTime;
import java.util.Date;

@TableName("erp_inventory_group2")
public class ErpProductInventory {

    @TableId(type = IdType.AUTO)
    @Excel(name = "记录ID")
    private Long id;

    @Excel(name = "订单编号")
    @TableField("order_code")
    private String orderCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "操作时间")
    @TableField("creation_time")
    private LocalDateTime creationTime;

    @Excel(name = "操作人")
    private String operator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "变更日期")
    @TableField("change_date")
    private Date changeDate;

    @Excel(name = "产品部件号")
    @TableField("product_part_number")
    private String productPartNumber;

    @Excel(name = "数量")
    @TableField("in_out_quantity")
    private Integer inOutQuantity;

    @Excel(name = "存储位置")
    @TableField("storage_location")
    private String storageLocation;

    @Excel(name = "备注")
    private String remarks;

    // Builder构造
    private ErpProductInventory(Builder builder) {
        this.id = builder.id;
        this.orderCode = builder.orderCode;
        this.creationTime = builder.creationTime;
        this.operator = builder.operator;
        this.changeDate = builder.changeDate;
        this.productPartNumber = builder.productPartNumber;
        this.inOutQuantity = builder.inOutQuantity;
        this.storageLocation = builder.storageLocation;
        this.remarks = builder.remarks;
    }

    public ErpProductInventory() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String orderCode;
        private LocalDateTime creationTime;
        private String operator;
        private Date changeDate;
        private String productPartNumber;
        private Integer inOutQuantity;
        private String storageLocation;
        private String remarks;

        // 链式方法（省略部分代码，参考字段赋值）
        public Builder id(Long id) { this.id = id; return this; }
        public Builder orderCode(String orderCode) { this.orderCode = orderCode; return this; }
        public Builder creationTime(LocalDateTime creationTime) { this.creationTime = creationTime; return this; }
        public Builder operator(String operator) { this.operator = operator; return this; }
        public Builder changeDate(Date changeDate) { this.changeDate = changeDate; return this; }
        public Builder productPartNumber(String productPartNumber) { this.productPartNumber = productPartNumber; return this; }
        public Builder inOutQuantity(Integer inOutQuantity) { this.inOutQuantity = inOutQuantity; return this; }
        public Builder storageLocation(String storageLocation) { this.storageLocation = storageLocation; return this; }
        public Builder remarks(String remarks) { this.remarks = remarks; return this; }

        public ErpProductInventory build() {
            return new ErpProductInventory(this);
        }
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

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public String getProductPartNumber() {
        return productPartNumber;
    }

    public void setProductPartNumber(String productPartNumber) {
        this.productPartNumber = productPartNumber;
    }

    public Integer getInOutQuantity() {
        return inOutQuantity;
    }

    public void setInOutQuantity(Integer inOutQuantity) {
        this.inOutQuantity = inOutQuantity;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}