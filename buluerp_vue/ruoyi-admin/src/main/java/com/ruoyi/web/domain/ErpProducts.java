package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

import java.time.LocalDateTime;

@TableName(value = "erp_products")
public class ErpProducts {

    /** $column.columnComment */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String name;

    /** $column.columnComment */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private LocalDateTime createTime;

    /** $column.columnComment */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private LocalDateTime updateTime;

    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String pictureUrl;

    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Integer designStatus;

    public Integer getDesignStatus() {
        return designStatus;
    }

    public void setDesignStatus(Integer designStatus) {
        this.designStatus = designStatus;
    }

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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime creatTime) {
        this.createTime = creatTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

}
