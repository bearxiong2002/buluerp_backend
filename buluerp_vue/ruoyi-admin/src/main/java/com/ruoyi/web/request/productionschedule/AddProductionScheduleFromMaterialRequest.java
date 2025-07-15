package com.ruoyi.web.request.productionschedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.validation.Save;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class AddProductionScheduleFromMaterialRequest {
    @ApiModelProperty(value = "设计总表ID", required = true)
    @NotNull(message = "设计总表ID未填写或无效", groups = Save.class)
    private Long designPatternId;

    @ApiModelProperty(value = "物料ID", required = true)
    @NotNull(message = "物料ID未填写或无效", groups = Save.class)
    private Long materialId;

    @ApiModelProperty(value = "布产时间，计划开始时间", required = true)
    @NotNull(message = "布产时间未填写或无效", groups = Save.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date productionTime;

    @ApiModelProperty(value = "颜色编码", required = true)
    @NotNull(message = "颜色编码未填写或无效", groups = Save.class)
    private String colorCode;

    @ApiModelProperty(value = "需要色粉数", required = true)
    @NotNull(groups = Save.class, message = "需要色粉数未填写或无效")
    private Long colorPowderNeeded;

    @ApiModelProperty(value = "用量", required = true)
    @NotNull(groups = Save.class, message = "用量未填写或无效")
    private Double usage;

    @ApiModelProperty(value = "布产数量", required = true)
    @NotNull(groups = Save.class, message = "布产数量未填写或无效")
    private Long productionQuantity;

    @ApiModelProperty(value = "布产模数", required = true)
    @NotNull(groups = Save.class, message = "布产模数未填写或无效")
    private Long productionMouldCount;

    @ApiModelProperty(value = "布产重量", required = true)
    @NotNull(message = "布产重量未填写或无效", groups = Save.class)
    private Double productionWeight;

    @ApiModelProperty("生产周期/s")
    private Double cycleTime;

    @ApiModelProperty("生产所需时间/h")
    private Double timeHours;

    @ApiModelProperty("出货时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date shipmentTime;

    @ApiModelProperty("原材料供应商")
    private String supplier;

    @ApiModelProperty("模具生产厂商")
    private String mouldManufacturer;

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public Long getDesignPatternId() {
        return designPatternId;
    }

    public void setDesignPatternId(Long designPatternId) {
        this.designPatternId = designPatternId;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public Date getProductionTime() {
        return productionTime;
    }

    public void setProductionTime(Date productionTime) {
        this.productionTime = productionTime;
    }

    public Long getColorPowderNeeded() {
        return colorPowderNeeded;
    }

    public void setColorPowderNeeded(Long colorPowderNeeded) {
        this.colorPowderNeeded = colorPowderNeeded;
    }

    public Double getUsage() {
        return usage;
    }

    public void setUsage(Double usage) {
        this.usage = usage;
    }

    public Long getProductionQuantity() {
        return productionQuantity;
    }

    public void setProductionQuantity(Long productionQuantity) {
        this.productionQuantity = productionQuantity;
    }

    public Long getProductionMouldCount() {
        return productionMouldCount;
    }

    public void setProductionMouldCount(Long productionMouldCount) {
        this.productionMouldCount = productionMouldCount;
    }

    public Double getProductionWeight() {
        return productionWeight;
    }

    public void setProductionWeight(Double productionWeight) {
        this.productionWeight = productionWeight;
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
}
