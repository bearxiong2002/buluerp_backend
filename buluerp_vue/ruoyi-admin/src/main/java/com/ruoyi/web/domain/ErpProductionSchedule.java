package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@TableName("erp_production_schedule")
@ApiModel("布产")
public class ErpProductionSchedule {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(notes = "仅响应")
    private Long id;

    @Excel(name = "订单编号")
    @ApiModelProperty(value = "订单编号", dataType = "String", required = true)
    @TableField(condition = SqlCondition.LIKE)
    private String orderCode;

    @Excel(name = "创建时间")
    @ApiModelProperty(value = "创建时间", dataType = "Date")
    @TableField(condition = BaseEntity.DATE_SQL_CONDITION)
    private Date creationTime;

    @Excel(name = "操作人")
    @ApiModelProperty(value = "操作人", dataType = "String", notes = "仅响应")
    @TableField(condition = SqlCondition.LIKE)
    private String operator;

    @Excel(name = "产品ID")
    @ApiModelProperty(value = "产品ID", dataType = "Long", required = true)
    private Long productId;

    @Excel(name = "布产时间")
    @ApiModelProperty(value = "布产时间", dataType = "Date")
    @TableField(condition = BaseEntity.DATE_SQL_CONDITION)
    private Date productionTime;

    @Excel(name = "产品编码")
    @ApiModelProperty(value = "产品编码", dataType = "String", required = true)
    @TableField(condition = SqlCondition.LIKE)
    private String productCode;

    @Excel(name = "模具编码")
    @ApiModelProperty(value = "模具编码", dataType = "String", required = true)
    @TableField(condition = SqlCondition.LIKE)
    private String mouldCode;

    @Excel(name = "模具状态")
    @ApiModelProperty(value = "模具状态", dataType = "String")
    @TableField(condition = SqlCondition.LIKE)
    private String mouldCondition;

    @Excel(name = "图片链接")
    @ApiModelProperty(value = "图片链接", dataType = "String", notes = "仅响应")
    @TableField(condition = SqlCondition.LIKE)
    private String pictureUrl;

    @Excel(name = "颜色编号")
    @ApiModelProperty(value = "颜色编号", dataType = "String")
    @TableField(condition = SqlCondition.LIKE)
    private String colorCode;

    @Excel(name = "用量(g)")
    @ApiModelProperty(value = "用量(g)", dataType = "Double")
    @TableField(value = "`usage`")
    private Double usage;

    @Excel(name = "材料类型")
    @ApiModelProperty(value = "材料类型", dataType = "String")
    @TableField(condition = SqlCondition.LIKE)
    private String materialType;

    @Excel(name = "腔数PCS")
    @ApiModelProperty(value = "腔数PCS", dataType = "Integer")
    private Integer cavityCount;

    @Excel(name = "单重(g)")
    @ApiModelProperty(value = "单重(g)", dataType = "Double")
    private Double singleWeight;

    @Excel(name = "布产数量PCS")
    @ApiModelProperty(value = "布产数量PCS", dataType = "Integer")
    private Integer productionQuantity;

    @Excel(name = "布产模数PCS")
    @ApiModelProperty(value = "布产模数PCS", dataType = "String")
    @TableField(condition = SqlCondition.LIKE)
    private Integer productionMouldCount;

    @Excel(name = "布产重量(kg)")
    @ApiModelProperty(value = "布产重量(kg)", dataType = "Double")
    private Double productionWeight;

    @Excel(name = "需要色粉份数")
    @ApiModelProperty(value = "需要色粉份数", dataType = "Integer")
    private Integer colorPowderNeeded;

    @Excel(name = "生产周期(s)")
    @ApiModelProperty(value = "生产周期(s)", dataType = "Double")
    private Double cycleTime;

    @Excel(name = "生产所需时间(h)")
    @ApiModelProperty(value = "生产所需时间(h)", dataType = "Double")
    private Double timeHours;

    @Excel(name = "出货时间")
    @ApiModelProperty(value = "出货时间", dataType = "Date")
    @TableField(condition = BaseEntity.DATE_SQL_CONDITION)
    private Date shipmentTime;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商", dataType = "String")
    @TableField(condition = SqlCondition.LIKE)
    private String supplier;

    @Excel(name = "模具厂家")
    @ApiModelProperty(value = "模具厂家", dataType = "String")
    @TableField(condition = SqlCondition.LIKE)
    private String mouldManufacturer;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户", dataType = "String")
    @TableField(condition = SqlCondition.LIKE)
    private String customer;

    @Excel(name = "库存")
    @ApiModelProperty(value = "库存", dataType = "Integer")
    private Integer inventory;

    @TableField(exist = false)
    @JsonIgnore
    @ApiModelProperty(value = "图片", dataType = "MultipartFile", notes = "仅请求")
    private MultipartFile picture;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料ID列表", dataType = "List<Long>")
    private List<Long> materialIds;

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

    public Date getProductionTime() {
        return productionTime;
    }

    public void setProductionTime(Date productionTime) {
        this.productionTime = productionTime;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getMouldCode() {
        return mouldCode;
    }

    public void setMouldCode(String mouldCode) {
        this.mouldCode = mouldCode;
    }

    public String getMouldCondition() {
        return mouldCondition;
    }

    public void setMouldCondition(String mouldCondition) {
        this.mouldCondition = mouldCondition;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public Double getUsage() {
        return usage;
    }

    public void setUsage(Double usage) {
        this.usage = usage;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public Integer getCavityCount() {
        return cavityCount;
    }

    public void setCavityCount(Integer cavityCount) {
        this.cavityCount = cavityCount;
    }

    public Double getSingleWeight() {
        return singleWeight;
    }

    public void setSingleWeight(Double singleWeight) {
        this.singleWeight = singleWeight;
    }

    public Integer getProductionQuantity() {
        return productionQuantity;
    }

    public void setProductionQuantity(Integer productionQuantity) {
        this.productionQuantity = productionQuantity;
    }

    public Integer getProductionMouldCount() {
        return productionMouldCount;
    }

    public void setProductionMouldCount(Integer productionMouldCount) {
        this.productionMouldCount = productionMouldCount;
    }

    public Double getProductionWeight() {
        return productionWeight;
    }

    public void setProductionWeight(Double productionWeight) {
        this.productionWeight = productionWeight;
    }

    public Integer getColorPowderNeeded() {
        return colorPowderNeeded;
    }

    public void setColorPowderNeeded(Integer colorPowderNeeded) {
        this.colorPowderNeeded = colorPowderNeeded;
    }

    public Double getCycleTime() {
        return cycleTime;
    }

    public void setCycleTime(Double cycleTime) {
        this.cycleTime = cycleTime;
    }

    public Double getTimeHours() {
        return timeHours;
    }

    public void setTimeHours(Double timeHours) {
        this.timeHours = timeHours;
    }

    public Date getShipmentTime() {
        return shipmentTime;
    }

    public void setShipmentTime(Date shipmentTime) {
        this.shipmentTime = shipmentTime;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getMouldManufacturer() {
        return mouldManufacturer;
    }

    public void setMouldManufacturer(String mouldManufacturer) {
        this.mouldManufacturer = mouldManufacturer;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    public MultipartFile getPicture() {
        return picture;
    }

    public void setPicture(MultipartFile picture) {
        this.picture = picture;
    }

    public List<Long> getMaterialIds() {
        return materialIds;
    }

    public void setMaterialIds(List<Long> materialIds) {
        this.materialIds = materialIds;
    }
}
