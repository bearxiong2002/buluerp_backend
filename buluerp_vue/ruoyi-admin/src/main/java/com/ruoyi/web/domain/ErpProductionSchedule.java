package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.validation.Save;
import com.ruoyi.common.validation.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.math3.analysis.function.Add;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@TableName("erp_production_schedule")
@ApiModel("布产")
public class ErpProductionSchedule {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(notes = "[GET|list|PUT|DELETE|response]")
    private Long id;

    @Excel(name = "订单编号")
    @ApiModelProperty(value = "订单编号", dataType = "String", required = true)
    @TableField(condition = SqlCondition.LIKE)
    @Example("BLK20250528000001 [list|POST|PUT|response]")
    @NotNull(message = "订单编号不能为空", groups = {Save.class})
    private String orderCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", type = Excel.Type.EXPORT, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间 [list|response]", dataType = "Date")
    @TableField(condition = BaseEntity.DATE_SQL_CONDITION)
    private Date creationTime;

    @Excel(name = "操作人", type = Excel.Type.EXPORT)
    @ApiModelProperty(value = "操作人 [list|response]", dataType = "String", notes = "仅响应")
    @TableField(condition = SqlCondition.LIKE)
    private String operator;

    @Excel(name = "产品ID")
    @ApiModelProperty(value = "产品ID [list|POST|PUT|response]", dataType = "Long", required = true)
    @Example("1")
    @NotNull(groups = {Save.class}, message = "产品ID不能为空")
    private Long productId;

    @Excel(name = "排产ID")
    @Example("1")
    @ApiModelProperty(value = "对应排产ID [list|POST|PUT|response]", dataType = "Long")
    // @NotNull(groups = Save.class, message = "排产ID无效")
    @Range(min = 1, message = "排产ID不能小于1", groups = {Save.class, Update.class})
    private Long arrangeId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "布产时间", dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "布产时间 [list|POST|PUT|response]", dataType = "Date")
    @TableField(condition = BaseEntity.DATE_SQL_CONDITION)
    @Example("2022-05-28")
    private Date productionTime;

    @Excel(name = "产品编码")
    @ApiModelProperty(value = "产品编码 [list|POST|PUT|response]", dataType = "String")
    @TableField(condition = SqlCondition.LIKE)
    @Example("123456")
    private String productCode;

    @Excel(name = "模具编码")
    @ApiModelProperty(value = "模具编码 [list|POST|PUT|response]", dataType = "String", required = true)
    @TableField(condition = SqlCondition.LIKE)
    @Example("客户")
    @NotNull(message = "模具编码不能为空", groups = {Save.class})
    private String mouldNumber;

    @Excel(name = "模具状态")
    @ApiModelProperty(value = "模具状态 [list|POST|PUT|response]", dataType = "String")
    @TableField(condition = SqlCondition.LIKE)
    @Example("正常")
    private String mouldCondition;

    @Excel(name = "图片链接", cellType = Excel.ColumnType.IMAGE)
    @ApiModelProperty(value = "图片链接 [list|POST|PUT|response]", dataType = "String", notes = "仅响应")
    @TableField(condition = SqlCondition.LIKE)
    @Example
    private String pictureUrl;

    @Excel(name = "颜色编号")
    @ApiModelProperty(value = "颜色编号 [list|POST|PUT|response]", dataType = "String")
    @TableField(condition = SqlCondition.LIKE)
    @Example("123,456,789")
    private String colorCode;

    @Excel(name = "用量(g)")
    @ApiModelProperty(value = "用量(g) [list|POST|PUT|response]", dataType = "Double")
    @TableField(value = "`usage`")
    @Range(min = 0, message = "用量(g)必须大于0", groups = {Save.class, Update.class})
    @Example("123.456")
    private Double usage;

    @Excel(name = "材料类型")
    @ApiModelProperty(value = "材料类型 [list|POST|PUT|response]", dataType = "String")
    @TableField(condition = SqlCondition.LIKE)
    @Example("YL1234567")
    private String materialType;

    @Excel(name = "腔数PCS")
    @ApiModelProperty(value = "腔数PCS", dataType = "Integer")
    @Range(min = 0, message = "腔数PCS必须大于0", groups = {Save.class, Update.class})
    @Example("12")
    private Integer cavityCount;

    @Excel(name = "单重(g)")
    @ApiModelProperty(value = "单重(g) [list|POST|PUT|response]", dataType = "Double")
    @Range(min = 0, message = "单重(g)必须大于0", groups = {Save.class, Update.class})
    @Example("123.456")
    private Double singleWeight;

    @Excel(name = "布产数量PCS")
    @ApiModelProperty(value = "布产数量PCS [list|POST|PUT|response]", dataType = "Integer")
    @Range(min = 1, message = "布产数量PCS必须大于1", groups = {Save.class, Update.class})
    @Example("12")
    private Integer productionQuantity;

    @Excel(name = "布产模数PCS")
    @ApiModelProperty(value = "布产模数PCS [list|POST|PUT|response]", dataType = "String")
    @TableField(condition = SqlCondition.LIKE)
    @Range(min = 0, message = "布产模数PCS必须大于0", groups = {Save.class, Update.class})
    @Example("12")
    private Integer productionMouldCount;

    @Excel(name = "布产重量(kg)")
    @ApiModelProperty(value = "布产重量(kg) [list|POST|PUT|response]", dataType = "Double")
    @Range(min = 0, message = "布产重量(kg)必须大于0", groups = {Save.class, Update.class})
    @Example("123.456")
    private Double productionWeight;

    @Excel(name = "需要色粉份数")
    @ApiModelProperty(value = "需要色粉份数 [list|POST|PUT|response]", dataType = "Integer")
    @Range(min = 0, message = "需要色粉份数必须大于0", groups = {Save.class, Update.class})
    @Example("12")
    private Integer colorPowderNeeded;

    @Excel(name = "生产周期(s)")
    @ApiModelProperty(value = "生产周期(s) [list|POST|PUT|response]", dataType = "Double")
    @Range(min = 0, message = "生产周期(s)必须大于0", groups = {Save.class, Update.class})
    @Example("123.456")
    private Double cycleTime;

    @Excel(name = "生产所需时间(h)")
    @ApiModelProperty(value = "生产所需时间(h) [list|POST|PUT|response]", dataType = "Double")
    @Range(min = 0, message = "生产所需时间(h)必须大于0", groups = {Save.class, Update.class})
    @Example("123.456")
    private Double timeHours;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "出货时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "出货时间 [list|POST|PUT|response]", dataType = "Date")
    @TableField(condition = BaseEntity.DATE_SQL_CONDITION)
    @Example("2022-05-28 10:00:00")
    private Date shipmentTime;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商 [list|POST|PUT|response]", dataType = "String")
    @TableField(condition = SqlCondition.LIKE)
    @Example("凝聚")
    private String supplier;

    @Excel(name = "模具厂家")
    @ApiModelProperty(value = "模具厂家 [list|POST|PUT|response]", dataType = "String")
    @TableField(condition = SqlCondition.LIKE)
    @Example("布鲁科")
    private String mouldManufacturer;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户 [list|POST|PUT|response]", dataType = "String")
    @TableField(condition = SqlCondition.LIKE)
    @Example("张三")
    private String customer;

    @Excel(name = "库存")
    @ApiModelProperty(value = "库存 [list|POST|PUT|response]", dataType = "Integer")
    @Range(min = 0, message = "库存必须大于0", groups = {Save.class, Update.class})
    @Example("12")
    private Integer inventory;

    @TableField(exist = false)
    @JsonIgnore
    @ApiModelProperty(value = "图片 [POST|PUT]", dataType = "MultipartFile", notes = "仅请求")
    private MultipartFile picture;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料ID列表 [response]", dataType = "List<Long>")
    private List<Long> materialIds;
    @ApiModelProperty(value = "布产状态", dataType = "Long")
    private Long status;

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
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

    public String getMouldNumber() {
        return mouldNumber;
    }

    public void setMouldNumber(String mouldNumber) {
        this.mouldNumber = mouldNumber;
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

    public @Range(min = 1, message = "排产ID不能小于1", groups = {Save.class, Update.class}) Long getArrangeId() {
        return arrangeId;
    }

    public void setArrangeId(@Range(min = 1, message = "排产ID不能小于1", groups = {Save.class, Update.class}) Long arrangeId) {
        this.arrangeId = arrangeId;
    }
}
