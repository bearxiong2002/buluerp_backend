package com.ruoyi.web.request.mould;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.validation.NullOrNotBlank;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class UpdateMouldRequest {
    @ApiModelProperty(value = "模具ID")
    @NotNull(message = "模具ID不能为空")
    private Long id;

    @ApiModelProperty(value = "模具状态：创建(待制作)/制作完成(待验收)/验收通过/试模完成")
    @NullOrNotBlank(message = "模具状态不能为空")
    private String status;

    @ApiModelProperty(value = "试模日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date trialDate;

    @ApiModelProperty(value = "厂商ID")
    private Long manufacturerId;

    @ApiModelProperty(value = "模房ID（传负数代表从模房移除）")
    private Long mouldHouseId;

    public Long getMouldHouseId() {
        return mouldHouseId;
    }

    public void setMouldHouseId(Long mouldHouseId) {
        this.mouldHouseId = mouldHouseId;
    }

    public Long getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(Long manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public Date getTrialDate() {
        return trialDate;
    }

    public void setTrialDate(Date trialDate) {
        this.trialDate = trialDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
