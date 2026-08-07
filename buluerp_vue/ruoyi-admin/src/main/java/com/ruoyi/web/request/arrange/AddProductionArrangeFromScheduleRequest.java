package com.ruoyi.web.request.arrange;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.validation.Save;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class AddProductionArrangeFromScheduleRequest {
    @ApiModelProperty(value = "布产ID")
    @NotNull(message = "布产ID不能为空", groups = {Save.class})
    private Long scheduleId;

    @ApiModelProperty(value = "布产ID表达式(供Excel使用)", hidden = true)
    @JsonIgnore
    @Excel(name = "布产ID")
    @Example("1")
    private String scheduleIdExpr;

    @AssertTrue(message = "布产ID未填写", groups = {Save.class})
    public boolean isScheduleIdValid() {
        return scheduleId != null || scheduleIdExpr != null;
    }

    @ApiModelProperty(value = "排产图片URL")
    @Excel(name = "排产图片", cellType = Excel.ColumnType.IMAGE)
    private String pictureUrl;

    @ApiModelProperty(value = "排产图片文件")
    private MultipartFile pictureFile;

    @ApiModelProperty(value = "出模数")
    @Excel(name = "出模数")
    @Example("1")
    private Long mouldOutput;

    @ApiModelProperty(value = "安排时间")
    @Excel(name = "安排时间", dateFormat = "yyyy-MM-dd")
    @Example("2023-01-01")
    private Date scheduledTime;

    @ApiModelProperty(value = "完成时间")
    @Excel(name = "完成时间", dateFormat = "yyyy-MM-dd")
    @Example("2023-01-01")
    private Date completionTime;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    @Example("备注")
    private String remarks;

    public void makeScheduleIdExpr() {
        if (scheduleId != null) {
            scheduleIdExpr = scheduleId.toString();
        }
    }

    public void parseScheduleIdExpr() {
        if (scheduleIdExpr != null) {
            String trimmed = scheduleIdExpr.trim();
            if (!trimmed.isEmpty()) {
                try {
                    scheduleId = Long.parseLong(trimmed);
                } catch (NumberFormatException e) {
                    scheduleId = null;
                }
            }
        }
    }

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

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getScheduleIdExpr() {
        return scheduleIdExpr;
    }

    public void setScheduleIdExpr(String scheduleIdExpr) {
        this.scheduleIdExpr = scheduleIdExpr;
    }

    public Date getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(Date completionTime) {
        this.completionTime = completionTime;
    }
}
