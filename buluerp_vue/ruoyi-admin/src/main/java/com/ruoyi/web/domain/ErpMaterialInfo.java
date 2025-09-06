package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.validation.NullOrNotBlank;
import com.ruoyi.common.validation.Save;
import com.ruoyi.common.validation.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;
import java.util.List;

@TableName(value = "erp_material_info")
@ApiModel("物料资料")
public class ErpMaterialInfo extends BaseEntity {
    @TableId(type = IdType.AUTO)
    @Excel(name = "物料id")
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "创建时间", type = Excel.Type.EXPORT)
    private Date creatTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "更新时间", type = Excel.Type.EXPORT)
    private Date updateTime;

    @Excel(name = "胶件图引", cellType = Excel.ColumnType.IMAGE, height = 100)
    @Example
    private String drawingReference;

    @Excel(name = "模具编号")
    @Example("客户")
    @NotBlank(message = "模具编号不能为空", groups = {Save.class})
    private String mouldNumber;

    @Excel(name = "规格名称")
    @Example("123456789")
    private String specificationName;

    @Excel(name = "腔口数量")
    @Range(min = 0, message = "腔口数量不能为负数", groups = {Save.class, Update.class})
    @Example("2")
    private Integer cavityCount;

    @Excel(name = "料别")
    @Example("123,456,789")
    @NotBlank(message = "物料类型不能为空", groups = {Save.class})
    @NullOrNotBlank(message = "物料类型不能为空", groups = {Update.class}) // 被其他表引用，不允许修改
    private String materialType;

    @Excel(name = "常规编码")
    @Example("1234567890")
    private String standardCode;

    @Excel(name = "单重")
    @Range(min = 0, message = "单重不能为负数", groups = {Save.class, Update.class})
    @Example("2345689.0")
    private Double singleWeight;

    @Excel(name = "模具状态")
    @Example("已放产")
    private String mouldStatus;

    @Excel(name = "模具厂商")
    @Example("凝聚")
    private String mouldManufacturer;

    @Excel(name = "生产周期(秒)")
    @Range(min = 0, message = "生产周期不能为负数", groups = {Save.class, Update.class})
    @Example("10.0")
    private Double cycleTime;

    @Excel(name = "样品库位")
    @Example("A01")
    private String sampleLocation;

    @Excel(name = "备注")
    @Example("无")
    private String remarks;

    @Excel(name = "备用编码")
    @Example("-")
    private String spareCode;

    @Example(Example.CREATE_RECURSIVE)
    private ErpPurchaseInfo purchaseInfo;

    @ApiModelProperty("是否外购，用于查询参数和结果")
    private Boolean isPurchased;

    @JsonIgnore
    private MultipartFile drawingReferenceFile;

    @JsonIgnore
    @ApiModelProperty("[PUT]是否删除胶件图片")
    private Boolean deleteDrawingReference;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDrawingReference() {
        return drawingReference;
    }

    public void setDrawingReference(String drawingReference) {
        this.drawingReference = drawingReference;
    }

    public String getMouldNumber() {
        return mouldNumber;
    }

    public void setMouldNumber(String mouldNumber) {
        this.mouldNumber = mouldNumber;
    }

    public String getSpecificationName() {
        return specificationName;
    }

    public void setSpecificationName(String specificationName) {
        this.specificationName = specificationName;
    }

    public Integer getCavityCount() {
        return cavityCount;
    }

    public void setCavityCount(Integer cavityCount) {
        this.cavityCount = cavityCount;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getStandardCode() {
        return standardCode;
    }

    public void setStandardCode(String standardCode) {
        this.standardCode = standardCode;
    }

    public Double getSingleWeight() {
        return singleWeight;
    }

    public void setSingleWeight(Double singleWeight) {
        this.singleWeight = singleWeight;
    }

    public String getMouldStatus() {
        return mouldStatus;
    }

    public void setMouldStatus(String mouldStatus) {
        this.mouldStatus = mouldStatus;
    }

    public String getMouldManufacturer() {
        return mouldManufacturer;
    }

    public void setMouldManufacturer(String mouldManufacturer) {
        this.mouldManufacturer = mouldManufacturer;
    }

    public Double getCycleTime() {
        return cycleTime;
    }

    public void setCycleTime(Double cycleTime) {
        this.cycleTime = cycleTime;
    }

    public String getSampleLocation() {
        return sampleLocation;
    }

    public void setSampleLocation(String sampleLocation) {
        this.sampleLocation = sampleLocation;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getSpareCode() {
        return spareCode;
    }

    public void setSpareCode(String spareCode) {
        this.spareCode = spareCode;
    }

    public MultipartFile getDrawingReferenceFile() {
        return drawingReferenceFile;
    }

    public void setDrawingReferenceFile(MultipartFile drawingReferenceFile) {
        this.drawingReferenceFile = drawingReferenceFile;
    }

    public Boolean getDeleteDrawingReference() {
        return deleteDrawingReference;
    }

    public void setDeleteDrawingReference(Boolean deleteDrawingReference) {
        this.deleteDrawingReference = deleteDrawingReference;
    }

    public ErpPurchaseInfo getPurchaseInfo() {
        return purchaseInfo;
    }

    public void setPurchaseInfo(ErpPurchaseInfo purchaseInfo) {
        this.purchaseInfo = purchaseInfo;
    }

    public Boolean isPurchased() {
        return isPurchased;
    }

    public void setPurchased(Boolean purchased) {
        isPurchased = purchased;
    }
}
