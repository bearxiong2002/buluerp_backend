package com.ruoyi.web.request.mould;

import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AddMouldRequest {
    @ApiModelProperty(value = "模具编号")
    @NotBlank(message = "模具编号不能为空")
    @Excel(name = "模具编号")
    private String mouldNumber;

    @ApiModelProperty(value = "厂商名称")
    @NotNull(message = "厂商名称不能为空")
    @Excel(name = "生产商名称")
    private String manufacturerName;

    public String getMouldNumber() {
        return mouldNumber;
    }

    public void setMouldNumber(String mouldNumber) {
        this.mouldNumber = mouldNumber;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }
}
