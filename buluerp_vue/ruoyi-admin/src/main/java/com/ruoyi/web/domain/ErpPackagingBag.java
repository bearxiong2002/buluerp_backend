package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.validation.Save;
import com.ruoyi.common.validation.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel("分包袋")
public class ErpPackagingBag {
    @TableId(type = IdType.AUTO)
    @TableField(condition = SqlCondition.LIKE)
    @Excel(name = "分包序号")
    @ApiModelProperty("主键，唯一标识每条记录 [list|PUT|response]")
    private Long id;

    @Excel(name = "分包列表编号")
    @Example("1")
    @ApiModelProperty("分包表ID，本包属于的分包表ID [list|POST|PUT|response]")
    @NotNull(message = "分包列表编号不能为空", groups = {Save.class})
    private Long packagingListId;

    @Excel(name = "本包重量")
    @Example("10.0")
    @ApiModelProperty("本包重量 [list|POST|PUT|response]")
    @Range(min = 0, message = "本包重量不能小于0", groups = {Save.class, Update.class})
    private Double bagWeight;

    @Excel(name = "本包规格")
    @Example("ABC")
    @TableField(condition = SqlCondition.LIKE)
    @ApiModelProperty("本包规格 [list|POST|PUT|response]")
    private String bagSpecification;

    @Excel(name = "本包配件/种")
    @Example("10")
    @ApiModelProperty("本包配件/种 [list|POST|PUT|response]")
    @Range(min = 0, message = "本包配件/种不能小于0", groups = {Save.class, Update.class})
    private Integer bagAccessory;

    @Excel(name = "本包数量")
    @Example("10")
    @ApiModelProperty("本包数量 [list|POST|PUT|response]")
    @Range(min = 0, message = "本包数量不能小于0", groups = {Save.class, Update.class})
    private Integer bagQuantity;

    @TableField(exist = false)
    @ApiModelProperty("分包明细列表 [response]")
    private List<ErpPackagingDetail> details;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull(message = "分包列表编号不能为空", groups = {Save.class}) Long getPackagingListId() {
        return packagingListId;
    }

    public void setPackagingListId(@NotNull(message = "分包列表编号不能为空", groups = {Save.class}) Long packagingListId) {
        this.packagingListId = packagingListId;
    }

    public @Range(min = 0, message = "本包重量不能小于0", groups = {Save.class, Update.class}) Double getBagWeight() {
        return bagWeight;
    }

    public void setBagWeight(@Range(min = 0, message = "本包重量不能小于0", groups = {Save.class, Update.class}) Double bagWeight) {
        this.bagWeight = bagWeight;
    }

    public String getBagSpecification() {
        return bagSpecification;
    }

    public void setBagSpecification(String bagSpecification) {
        this.bagSpecification = bagSpecification;
    }

    public @Range(min = 0, message = "本包配件/种不能小于0", groups = {Save.class, Update.class}) Integer getBagAccessory() {
        return bagAccessory;
    }

    public void setBagAccessory(@Range(min = 0, message = "本包配件/种不能小于0", groups = {Save.class, Update.class}) Integer bagAccessory) {
        this.bagAccessory = bagAccessory;
    }

    public @Range(min = 0, message = "本包数量不能小于0", groups = {Save.class, Update.class}) Integer getBagQuantity() {
        return bagQuantity;
    }

    public void setBagQuantity(@Range(min = 0, message = "本包数量不能小于0", groups = {Save.class, Update.class}) Integer bagQuantity) {
        this.bagQuantity = bagQuantity;
    }

    public List<ErpPackagingDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ErpPackagingDetail> details) {
        this.details = details;
    }
}
