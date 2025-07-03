package com.ruoyi.web.request.arrange;

import com.ruoyi.common.validation.Save;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

public class AddProductionArrangeFromScheduleRequest {
    @ApiModelProperty(value = "布产ID列表")
    @NotNull(message = "布产ID列表不能为空", groups = {Save.class})
    @Size(min = 1, message = "布产ID列表不能为空", groups = {Save.class})
    private List<Long> scheduleIds;

    @ApiModelProperty(value = "排产图片URL")
    private String pictureUrl;

    @ApiModelProperty(value = "排产图片文件")
    private MultipartFile pictureFile;

    @ApiModelProperty(value = "出模数")
    private Long mouldOutput;

    @ApiModelProperty(value = "安排时间")
    private Date scheduledTime;

    @ApiModelProperty(value = "备注")
    private String remarks;

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public MultipartFile getPictureFile() {
        return pictureFile;
    }

    public void setPictureFile(MultipartFile pictureFile) {
        this.pictureFile = pictureFile;
    }

    public Date getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Date scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getMouldOutput() {
        return mouldOutput;
    }

    public void setMouldOutput(Long mouldOutput) {
        this.mouldOutput = mouldOutput;
    }

    public @NotNull(message = "布产ID列表不能为空", groups = {Save.class}) @Size(min = 1, message = "布产ID列表不能为空", groups = {Save.class}) List<Long> getScheduleIds() {
        return scheduleIds;
    }

    public void setScheduleIds(@NotNull(message = "布产ID列表不能为空", groups = {Save.class}) @Size(min = 1, message = "布产ID列表不能为空", groups = {Save.class}) List<Long> scheduleIds) {
        this.scheduleIds = scheduleIds;
    }
}
