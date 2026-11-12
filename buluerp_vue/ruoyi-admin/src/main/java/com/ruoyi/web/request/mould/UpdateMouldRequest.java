package com.ruoyi.web.request.mould;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.validation.NullOrNotBlank;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class UpdateMouldRequest {
    @ApiModelProperty(value = "模具ID")
    @NotNull(message = "模具ID不能为空")
    private Long id;

    @ApiModelProperty(value = "模具状态：创建(待制作)/制作完成(待验收)/验收通过/维修中/试模完成")
    @NullOrNotBlank(message = "模具状态不能为空")
    private String status;

    @ApiModelProperty(value = "试模日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date trialDate;

    @ApiModelProperty(value = "厂商名称")
    private String manufacturerName;

    @ApiModelProperty(value = "模房ID（传负数代表从模房移除）")
    private Long mouldHouseId;

    @ApiModelProperty(value = "模具图片文件，未上传时保留原图", dataType = "file")
    @JsonIgnore
    private MultipartFile picture;

    @ApiModelProperty(value = "是否删除模具图片，上传新图时优先使用新图")
    private Boolean deletePicture;

    public MultipartFile getPicture() { return picture; }
    public void setPicture(MultipartFile picture) { this.picture = picture; }
    public Boolean getDeletePicture() { return deletePicture; }
    public void setDeletePicture(Boolean deletePicture) { this.deletePicture = deletePicture; }

    public Long getMouldHouseId() {
        return mouldHouseId;
    }

    public void setMouldHouseId(Long mouldHouseId) {
        this.mouldHouseId = mouldHouseId;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
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
