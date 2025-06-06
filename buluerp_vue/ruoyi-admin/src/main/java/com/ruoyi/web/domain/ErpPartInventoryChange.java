package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

import java.time.LocalDateTime;
import java.util.Date;

@TableName("erp_part_inventory_change")
public class ErpPartInventoryChange {

    @TableId(type = IdType.AUTO)
    @Excel(name = "记录ID")
    private Integer id;

    @Excel(name = "订单编号")
    @TableField("order_code")
    private String orderCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "操作时间")
    @TableField("creation_time")
    private LocalDateTime creationTime;

    @Excel(name = "操作人")
    private String operator;

    @Excel(name = "模具编号")
    @TableField("mould_number")
    private String mouldNumber;

    @Excel(name = "颜色代码")
    @TableField("color_code")
    private String colorCode;

    @Excel(name = "数量")
    @TableField("in_out_quantity")
    private Integer inOutQuantity;

    @Excel(name = "备注")
    private String remarks;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "库存变更日期")
    @TableField("change_date")
    private Date changeDate;

    /** 当前总库存（不是数据库字段，用于查询时关联显示） */
    @TableField(exist = false)
    @Excel(name = "当前总库存")
    private Integer totalQuantity;

    // Builder构造
    private ErpPartInventoryChange(Builder builder) {
        this.id = builder.id;
        this.orderCode = builder.orderCode;
        this.creationTime = builder.creationTime;
        this.operator = builder.operator;
        this.mouldNumber = builder.mouldNumber;
        this.colorCode = builder.colorCode;
        this.inOutQuantity = builder.inOutQuantity;
        this.remarks = builder.remarks;
        this.changeDate = builder.changeDate;
        this.totalQuantity = builder.totalQuantity;
    }

    public ErpPartInventoryChange() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private String orderCode;
        private LocalDateTime creationTime;
        private String operator;
        private String mouldNumber;
        private String colorCode;
        private Integer inOutQuantity;
        private String remarks;
        private Date changeDate;
        private Integer totalQuantity;

        // 链式方法（省略部分代码，参考字段赋值）
        public Builder id(Integer id) { this.id = id; return this; }
        public Builder orderCode(String orderCode) { this.orderCode = orderCode; return this; }
        public Builder creationTime(LocalDateTime creationTime) { this.creationTime = creationTime; return this; }
        public Builder operator(String operator) { this.operator = operator; return this; }
        public Builder mouldNumber(String mouldNumber) { this.mouldNumber = mouldNumber; return this; }
        public Builder colorCode(String colorCode) { this.colorCode = colorCode; return this; }
        public Builder inOutQuantity(Integer inOutQuantity) { this.inOutQuantity = inOutQuantity; return this; }
        public Builder remarks(String remarks) { this.remarks = remarks; return this; }
        public Builder changeDate(Date changeDate) { this.changeDate = changeDate; return this; }
        public Builder totalQuantity(Integer totalQuantity) { this.totalQuantity = totalQuantity; return this; }

        public ErpPartInventoryChange build() {
            return new ErpPartInventoryChange(this);
        }
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

    public String getMouldNumber() {
        return mouldNumber;
    }

    public void setMouldNumber(String mouldNumber) {
        this.mouldNumber = mouldNumber;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public Integer getInOutQuantity() {
        return inOutQuantity;
    }

    public void setInOutQuantity(Integer inOutQuantity) {
        this.inOutQuantity = inOutQuantity;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
}