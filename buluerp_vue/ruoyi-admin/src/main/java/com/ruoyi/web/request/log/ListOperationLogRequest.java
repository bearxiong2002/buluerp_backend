package com.ruoyi.web.request.log;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel(value = "查询操作日志列表参数")
public class ListOperationLogRequest {
    @ApiModelProperty(value = "日志项ID")
    private Long id;

    @ApiModelProperty(value = "操作时间起始")
    private Date operationTimeFrom;

    @ApiModelProperty(value = "操作时间结束")
    private Date operationTimeTo;

    @ApiModelProperty(value = "操作类型")
    private String operationType;

    @ApiModelProperty(value = "操作对象记录的ID")
    private String recordId;

    @ApiModelProperty(value = "操作人（用户名）")
    private String operator;

    @ApiModelProperty(value = "操作详情")
    private String details;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getOperationTimeFrom() {
        return operationTimeFrom;
    }

    public void setOperationTimeFrom(Date operationTimeFrom) {
        this.operationTimeFrom = operationTimeFrom;
    }

    public Date getOperationTimeTo() {
        return operationTimeTo;
    }

    public void setOperationTimeTo(Date operationTimeTo) {
        this.operationTimeTo = operationTimeTo;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
