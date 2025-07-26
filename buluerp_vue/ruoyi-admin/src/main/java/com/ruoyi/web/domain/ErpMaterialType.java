package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.validation.NullOrNotBlank;
import com.ruoyi.common.validation.Save;
import com.ruoyi.common.validation.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.util.Date;

@ApiModel("物料类型")
public class ErpMaterialType {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键，唯一标识每条记录")
    @Excel(name = "主键", type = Excel.Type.EXPORT)
    private Long id;

    @TableField(condition = SqlCondition.LIKE)
    @ApiModelProperty(value = "名称，物料类型的名称", required = true)
    @Excel(name = "名称")
    @Example("料型A")
    @NotBlank(message = "名称不能为空", groups = Save.class)
    // @Null(message = "名称不能修改", groups = Update.class)
    private String name;

    @TableField(condition = SqlCondition.LIKE)
    @ApiModelProperty(value = "颜色编码", required = true)
    @Excel(name = "颜色编码")
    @Example("红色")
    @NotNull(message = "颜色编码不能为空", groups = Save.class)
    @NullOrNotBlank(message = "颜色编码不能为空", groups = Update.class)
    private String colorCode;

    @ApiModelProperty(value = "色粉重量", required = true)
    @Excel(name = "色粉重量")
    @Example("100.00")
    @NotNull(message = "色粉重量不能为空", groups = Save.class)
    private Double colorWeight;

    @TableField(condition = BaseEntity.DATE_SQL_CONDITION)
    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间", type = Excel.Type.EXPORT, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(condition = BaseEntity.DATE_SQL_CONDITION)
    @ApiModelProperty(value = "更新时间")
    @Excel(name = "更新时间", type = Excel.Type.EXPORT, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public Double getColorWeight() {
        return colorWeight;
    }

    public void setColorWeight(Double colorWeight) {
        this.colorWeight = colorWeight;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
