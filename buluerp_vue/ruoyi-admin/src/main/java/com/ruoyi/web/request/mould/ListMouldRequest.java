package com.ruoyi.web.request.mould;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@TableName("erp_mould")
public class ListMouldRequest {
    @ApiModelProperty(value = "模具ID")
    private Long id;

    @ApiModelProperty(value = "模具编号")
    private String mouldNumber;

    @ApiModelProperty(value = "模具名称")
    private String status;

    @ApiModelProperty(value = "试用日期开始")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date trialDateFrom;

    @ApiModelProperty(value = "试用日期结束")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date trialDateTo;

    @ApiModelProperty(value = "厂商ID")
    private Long manufacturerId;

    @ApiModelProperty(value = "模房ID（-1代表未分配）")
    private Long mouldHouseId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMouldNumber() {
        return mouldNumber;
    }

    public void setMouldNumber(String mouldNumber) {
        this.mouldNumber = mouldNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTrialDateFrom() {
        return trialDateFrom;
    }

    public void setTrialDateFrom(Date trialDateFrom) {
        this.trialDateFrom = trialDateFrom;
    }

    public Date getTrialDateTo() {
        return trialDateTo;
    }

    public void setTrialDateTo(Date trialDateTo) {
        this.trialDateTo = trialDateTo;
    }

    public Long getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(Long manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public Long getMouldHouseId() {
        return mouldHouseId;
    }

    public void setMouldHouseId(Long mouldHouseId) {
        this.mouldHouseId = mouldHouseId;
    }
}
