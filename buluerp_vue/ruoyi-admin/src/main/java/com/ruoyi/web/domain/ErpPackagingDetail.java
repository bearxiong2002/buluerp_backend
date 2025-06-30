package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.validation.Save;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@ApiModel("分包明细")
public class ErpPackagingDetail {
    @TableId(value = "id", type = IdType.AUTO)
    @TableField(condition = SqlCondition.LIKE)
    @Excel(name = "分包明细ID")
    @ApiModelProperty("主键，唯一标识每条记录 [list|PUT|response]")
    private Integer id;

    @TableField(condition = SqlCondition.LIKE)
    @Excel(name = "模具编号")
    @Example("ABC123")
    @ApiModelProperty("模具编号，用于标识模具 [list|POST|PUT|response]")
    @NotNull(groups = {Save.class}, message = "模具编号不能为空")
    private String mouldNumber;

    @TableField(condition = SqlCondition.LIKE)
    @Excel(name = "胶件图片")
    @Example
    @ApiModelProperty("胶件图片，模具零件的图片链接 [list|POST|PUT|response]")
    private String partImageUrl;

    @TableField(condition = SqlCondition.LIKE)
    @Excel(name = "料别")
    @Example("塑料")
    @ApiModelProperty("料别，材料的类型 [list|POST|PUT|response]")
    private String materialType;

    @TableField(condition = SqlCondition.LIKE)
    @Excel(name = "颜色编号")
    @Example("RGB")
    @ApiModelProperty("颜色编号，模具零件的颜色编码 [list|POST|PUT|response]")
    private String colorCode;

    @TableField(condition = SqlCondition.LIKE)
    @Excel(name = "规格名称")
    @Example("S1")
    @ApiModelProperty("规格名称，模具零件的规格描述 [list|POST|PUT|response]")
    private String specificationName;

    @TableField("`usage`")
    @Excel(name = "用量")
    @Example("0.5")
    @ApiModelProperty("用量，单位为克，表示单个零件的用料量 [list|POST|PUT|response]")
    @Range(min = 0, message = "用量不能为负数")
    private Double usage;

    @Excel(name = "单重")
    @Example("0.5")
    @ApiModelProperty("单重，单位为克，表示单个零件的重量 [list|POST|PUT|response]")
    @Range(min = 0, message = "单重不能为负数")
    private Double singleWeight;

    @Excel(name = "套料数量")
    @Example("10")
    @ApiModelProperty("套料数量，每套包含的零件数量 [list|POST|PUT|response]")
    @Range(min = 0, message = "套料数量不能为负数")
    private Integer setQuantity;

    @TableField(condition = SqlCondition.LIKE)
    @Excel(name = "备注")
    @ApiModelProperty("备注，其它需要说明的信息 [list|POST|PUT|response]")
    private String remarks;

    @Excel(name = "分包袋编号")
    @ApiModelProperty("分包袋编号，与分包袋主键关联 [list|POST|PUT|response]")
    @NotNull(message = "分包袋编号不能为空", groups = {Save.class})
    private Long packagingBagId;

    @TableField(exist = false)
    @JsonIgnore
    @ApiModelProperty("胶件图片文件 [POST|PUT]")
    private MultipartFile partImageFile;

    public MultipartFile getPartImageFile() {
        return partImageFile;
    }

    public void setPartImageFile(MultipartFile partImageFile) {
        this.partImageFile = partImageFile;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public @Range(min = 0, message = "套料数量不能为负数") Integer getSetQuantity() {
        return setQuantity;
    }

    public void setSetQuantity(@Range(min = 0, message = "套料数量不能为负数") Integer setQuantity) {
        this.setQuantity = setQuantity;
    }

    public @Range(min = 0, message = "单重不能为负数") Double getSingleWeight() {
        return singleWeight;
    }

    public void setSingleWeight(@Range(min = 0, message = "单重不能为负数") Double singleWeight) {
        this.singleWeight = singleWeight;
    }

    public @Range(min = 0, message = "用量不能为负数") Double getUsage() {
        return usage;
    }

    public void setUsage(@Range(min = 0, message = "用量不能为负数") Double usage) {
        this.usage = usage;
    }

    public String getSpecificationName() {
        return specificationName;
    }

    public void setSpecificationName(String specificationName) {
        this.specificationName = specificationName;
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

    public String getPartImageUrl() {
        return partImageUrl;
    }

    public void setPartImageUrl(String partImageUrl) {
        this.partImageUrl = partImageUrl;
    }

    public @NotNull(groups = {Save.class}, message = "模具编号不能为空") String getMouldNumber() {
        return mouldNumber;
    }

    public void setMouldNumber(@NotNull(groups = {Save.class}, message = "模具编号不能为空") String mouldNumber) {
        this.mouldNumber = mouldNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public @NotNull(message = "分包袋编号不能为空", groups = {Save.class}) Long getPackagingBagId() {
        return packagingBagId;
    }

    public void setPackagingBagId(@NotNull(message = "分包袋编号不能为空", groups = {Save.class}) Long packagingBagId) {
        this.packagingBagId = packagingBagId;
    }
}
