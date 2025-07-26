package com.ruoyi.web.request.arrange;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.annotation.Example;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.validation.Save;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AddProductionArrangeFromScheduleRequest {
    @ApiModelProperty(value = "布产ID列表")
    // @NotNull(message = "布产ID列表不能为空", groups = {Save.class})
    @Size(min = 1, message = "布产ID列表不能为空", groups = {Save.class})
    private List<Long> scheduleIds;

    @ApiModelProperty(value = "布产ID列表表达式(供Excel使用)", hidden = true)
    @JsonIgnore
    @Excel(name = "布产ID列表")
    @Example("1,2,3")
    private String scheduleIdsExpr;

    @AssertTrue(message = "布产ID列表未填写", groups = {Save.class})
    public boolean isScheduleIdsValid() {
        return scheduleIds != null || scheduleIdsExpr != null;
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
    @Excel(name = "安排时间")
    @Example("2023-01-01")
    private Date scheduledTime;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    @Example("备注")
    private String remarks;

    public void makeScheduleIdsExpr() {
        if (scheduleIds != null) {
            scheduleIdsExpr = String.join(",", scheduleIds.stream().map(Object::toString).toArray(String[]::new));
        }
    }

    public void parseScheduleIdsExpr() {
        if (scheduleIdsExpr != null) {
            scheduleIds = Arrays.stream(scheduleIdsExpr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(numString -> {
                    try {
                        return Long.parseLong(numString);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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

    public List<Long> getScheduleIds() {
        return scheduleIds;
    }

    public void setScheduleIds(List<Long> scheduleIds) {
        this.scheduleIds = scheduleIds;
    }

    public String getScheduleIdsExpr() {
        return scheduleIdsExpr;
    }

    public void setScheduleIdsExpr(String scheduleIdsExpr) {
        this.scheduleIdsExpr = scheduleIdsExpr;
    }
}
