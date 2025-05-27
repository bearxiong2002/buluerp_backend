package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.domain.validation.Save;
import com.ruoyi.common.domain.validation.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/*
create table buluerp.erp_production_arrange
(
    id                     int auto_increment comment '主键，唯一标识每条记录'
        primary key,
    order_code             varchar(50)                        not null comment '订单编码，唯一标识订单',
    creation_time          datetime default CURRENT_TIMESTAMP null comment '创建时间，记录创建时的时间戳',
    operator               varchar(50)                        not null comment '操作人，执行排产操作的人员',
    product_id             int                                not null comment '产品编号，产品在系统中的编号',
    production_id          int                                not null comment '布产编号，对应系统中布产编号',
    production_time        datetime                           null comment '布产时间，计划开始生产的时间',
    product_code           varchar(50)                        not null comment '产品编码，产品的具体编码',
    mould_number           varchar(50)                        null comment '模具编号，使用的模具编号',
    picture_url            varchar(255)                       null comment '图片，产品的图片链接',
    color_code             varchar(50)                        null comment '颜色编号，产品的颜色编码',
    material_type          varchar(50)                        null comment '料型，材料的类型',
    mould_output           int                                null comment '出模数(pcs)，每个模具的产出数量',
    single_weight          decimal(10, 3)                     null comment '单重(g)，单个产品的重量，单位为克',
    production_quantity    int                                null comment '布产数量(pcs)，计划生产的总数量',
    production_mould_count int                                null comment '布产模数（模），计划使用的模具数量',
    production_weight      decimal(10, 3)                     null comment '布产重量(KG)，计划生产的总重量，单位为千克',
    remarks                text                               null comment '备注，其他需要说明的信息',
    scheduled_time         datetime                           null comment '安排时间，计划安排生产的时间',
    completion_time        datetime                           null comment '完成时间，实际完成生产的时间'
)
    comment '排产表，用于记录生产计划的详细信息';




* */

@TableName("erp_production_arrange")
@ApiModel("排产")
public class ErpProductionArrange {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(notes = "仅响应")
    private Long id;

    @Excel(name = "订单编码")
    @ApiModelProperty(value = "订单编码，唯一标识订单", dataType = "String")
    @TableField(condition = SqlCondition.LIKE)
    private String orderCode;

    @Excel(name = "创建时间")
    @ApiModelProperty(value = "创建时间，记录创建时的时间戳", dataType = "Date")
    @TableField(condition = BaseEntity.DATE_SQL_CONDITION)
    private Date creationTime;

    @Excel(name = "操作人")
    @ApiModelProperty(value = "操作人，执行排产操作的人员", dataType = "String")
    @TableField(condition = SqlCondition.LIKE)
    private String operator;

    @Excel(name = "产品编号")
    @ApiModelProperty(value = "产品编号，产品在系统中的编号", dataType = "String", required = true)
    private Long productId;

    @Excel(name = "布产编号")
    @ApiModelProperty(value = "布产编号", dataType = "Integer", required = true)
    private Integer productionId;

    @Excel(name = "布产时间")
    @ApiModelProperty(value = "布产时间，计划开始生产的时间", dataType = "Date")
    @TableField(condition = BaseEntity.DATE_SQL_CONDITION)
    private Date productionTime;

    @Excel(name = "产品编码")
    @ApiModelProperty(value = "产品编码，产品的具体编码", dataType = "String", required = true)
    @TableField(condition = SqlCondition.LIKE)
    private String productCode;

    @Excel(name = "模具编号")
    @ApiModelProperty(value = "模具编号，使用的模具编号", dataType = "String")
    @TableField(condition = SqlCondition.LIKE)
    private String mouldNumber;

    @Excel(name = "图片链接", cellType = Excel.ColumnType.IMAGE)
    @ApiModelProperty(value = "图片，产品的图片链接", dataType = "String")
    @TableField(condition = SqlCondition.LIKE)
    private String pictureUrl;

    @Excel(name = "颜色编号")
    @ApiModelProperty(value = "颜色编号，产品的颜色编码", dataType = "String")
    @TableField(condition = SqlCondition.LIKE)
    private String colorCode;

    @Excel(name = "料型")
    @ApiModelProperty(value = "料型，材料的类型", dataType = "String")
    @TableField(condition = SqlCondition.LIKE)
    private String materialType;

    @Excel(name = "出模数(pcs)")
    @ApiModelProperty(value = "出模数(pcs)，每个模具的产出数量", dataType = "Integer")
    @Range(min = 0, message = "出模数(pcs)不能小于0", groups = {Save.class, Update.class})
    private Integer mouldOutput;

    @Excel(name = "单重(g)")
    @ApiModelProperty(value = "单重(g)，单个产品的重量，单位为克", dataType = "Double")
    @Range(min = 0, message = "单重(g)不能小于0", groups = {Save.class, Update.class})
    private Double singleWeight;

    @Excel(name = "布产数量(pcs)")
    @ApiModelProperty(value = "布产数量(pcs)，计划生产的总数量", dataType = "Integer")
    @Range(min = 1, message = "布产数量(pcs)不能小于1", groups = {Save.class, Update.class})
    private Integer productionQuantity;

    @Excel(name = "布产模数（模）")
    @ApiModelProperty(value = "布产模数（模），计划使用的模具数量", dataType = "Integer")
    @Range(min = 1, message = "布产模数（模）不能小于1", groups = {Save.class, Update.class})
    private Integer productionMouldCount;

    @Excel(name = "布产重量(KG)")
    @ApiModelProperty(value = "布产重量(KG)，计划生产的总重量，单位为千克", dataType = "Double")
    @Range(min = 0, message = "布产重量(KG)不能小于0", groups = {Save.class, Update.class})
    private Double productionWeight;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注，其他需要说明的信息", dataType = "String")
    private String remarks;

    @Excel(name = "安排时间")
    @ApiModelProperty(value = "安排时间，计划安排生产的时间", dataType = "Date")
    @TableField(condition = BaseEntity.DATE_SQL_CONDITION)
    private Date scheduledTime;

    @Excel(name = "完成时间")
    @ApiModelProperty(value = "完成时间，实际完成生产的时间", dataType = "Date")
    @TableField(condition = BaseEntity.DATE_SQL_CONDITION)
    private Date completionTime;

    @ApiModelProperty(value = "图片", dataType = "MultipartFile")
    @JsonIgnore
    @TableField(exist = false)
    private MultipartFile picture;

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

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public Integer getMouldOutput() {
        return mouldOutput;
    }

    public void setMouldOutput(Integer mouldOutput) {
        this.mouldOutput = mouldOutput;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Date scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public Date getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(Date completionTime) {
        this.completionTime = completionTime;
    }

    public MultipartFile getPicture() {
        return picture;
    }

    public void setPicture(MultipartFile picture) {
        this.picture = picture;
    }

    public Integer getProductionId() {
        return productionId;
    }

    public void setProductionId(Integer productionId) {
        this.productionId = productionId;
    }
}
