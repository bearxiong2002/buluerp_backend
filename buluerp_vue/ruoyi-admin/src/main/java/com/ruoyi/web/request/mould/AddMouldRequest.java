package com.ruoyi.web.request.mould;

import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.validation.Save;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.groups.Default;

public class AddMouldRequest {
    @ApiModelProperty(value = "模具编号")
    @NotBlank(message = "模具编号不能为空", groups = {Default.class, Save.class})
    @Excel(name = "模具编号")
    @Example(value = "123456")
    private String mouldNumber;

    @ApiModelProperty(value = "厂商名称")
    @NotBlank(message = "厂商名称不能为空", groups = {Default.class, Save.class})
    @Excel(name = "生产商名称")
    @Example(value = "ABC")
    private String manufacturerName;

    @ApiModelProperty(value = "模具图片，Excel中插入图片，选填")
    @Excel(name = "模具图片", cellType = Excel.ColumnType.IMAGE, height = 80)
    @Example
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String pictureUrl;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    @Example
    private String remark;

    @ApiModelProperty(value = "模具图片文件", dataType = "file")
    @JsonIgnore
    private MultipartFile picture;

    public String getPictureUrl() { return pictureUrl; }
    public void setPictureUrl(String pictureUrl) { this.pictureUrl = pictureUrl; }
    public MultipartFile getPicture() { return picture; }
    public void setPicture(MultipartFile picture) { this.picture = picture; }

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
