package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

import java.time.LocalDateTime;
import java.util.Date;

@TableName("erp_packaging_material_inventory_change")
public class ErpPackagingMaterialInventoryChange {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    @Excel(name = "记录ID")
    private Integer id;

    /** 订单编号 */
    @Excel(name = "订单编号")
    @TableField("order_code")
    private String orderCode;

    /** 操作时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "操作时间")
    @TableField("creation_time")
    private LocalDateTime creationTime;

    /** 操作人 */
    @Excel(name = "操作人")
    private String operator;

    /** 变更日期 */
    @Excel(name = "变更日期")
    @TableField("change_date")
    private Date changeDate;

    /** 编辑动作（例如：1=入库，2=出库） */
    @Excel(name = "操作信息")
    @TableField("edit_action")
    private String editAction;

    /** 产品部件号 */
    @Excel(name = "产品货号")
    @TableField("product_part_number")
    private String productPartNumber;

    /** 包装编号 */
    @Excel(name = "包装编号")
    @TableField("packaging_number")
    private String packagingNumber;

    /** 出入库数量 */
    @Excel(name = "数量")
    @TableField("in_out_quantity")
    private Integer inOutQuantity;

    /** 库位信息 */
    @Excel(name = "存储位置")
    @TableField("storage_location")
    private String storageLocation;

    /** 备注信息 */
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

    public String getEditAction() {
        return editAction;
    }

    public void setEditAction(String editAction) {
        this.editAction = editAction;
    }

    public String getProductPartNumber() {
        return productPartNumber;
    }

    public void setProductPartNumber(String productPartNumber) {
        this.productPartNumber = productPartNumber;
    }

    public String getPackagingNumber() {
        return packagingNumber;
    }

    public void setPackagingNumber(String packagingNumber) {
        this.packagingNumber = packagingNumber;
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

    // 私有构造方法（Builder专用）
    private ErpPackagingMaterialInventoryChange(Builder builder) {
        this.id = builder.id;
        this.orderCode = builder.orderCode;
        this.creationTime = builder.creationTime;
        this.operator = builder.operator;
        this.changeDate = builder.changeDate;
        this.editAction = builder.editAction;
        this.productPartNumber = builder.productPartNumber;
        this.packagingNumber = builder.packagingNumber;
        this.inOutQuantity = builder.inOutQuantity;
        this.storageLocation = builder.storageLocation;
        this.remarks = builder.remarks;
    }

    // 空参构造方法（MyBatis等框架需要）
    public ErpPackagingMaterialInventoryChange() {}

    // 静态Builder入口
    public static Builder builder() {
        return new Builder();
    }

    // Builder内部类
    public static class Builder {
        private Integer id;
        private String orderCode;
        private LocalDateTime creationTime;
        private String operator;
        private Date changeDate;
        private String editAction;
        private String productPartNumber;
        private String packagingNumber;
        private Integer inOutQuantity;
        private String storageLocation;
        private String remarks;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder orderCode(String orderCode) {
            this.orderCode = orderCode;
            return this;
        }

        public Builder creationTime(LocalDateTime creationTime) {
            this.creationTime = creationTime;
            return this;
        }

        public Builder operator(String operator) {
            this.operator = operator;
            return this;
        }

        public Builder changeDate(Date changeDate) {
            this.changeDate = changeDate;
            return this;
        }

        public Builder editAction(String editAction) {
            this.editAction = editAction;
            return this;
        }

        public Builder productPartNumber(String productPartNumber) {
            this.productPartNumber = productPartNumber;
            return this;
        }

        public Builder packagingNumber(String packagingNumber) {
            this.packagingNumber = packagingNumber;
            return this;
        }

        public Builder inOutQuantity(Integer inOutQuantity) {
            this.inOutQuantity = inOutQuantity;
            return this;
        }

        public Builder storageLocation(String storageLocation) {
            this.storageLocation = storageLocation;
            return this;
        }

        public Builder remarks(String remarks) {
            this.remarks = remarks;
            return this;
        }

        public ErpPackagingMaterialInventoryChange build() {
            return new ErpPackagingMaterialInventoryChange(this);
        }
    }
}