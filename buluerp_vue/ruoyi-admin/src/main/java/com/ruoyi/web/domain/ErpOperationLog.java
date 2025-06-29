package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.web.log.OperationLog;

import java.util.Date;

public class ErpOperationLog {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Excel(name = "操作时间")
    private Date operationTime;

    @Excel(name = "业务类型")
    private String operationType;

    @Excel(name = "记录ID")
    private String recordId;

    @Excel(name = "操作人")
    private String operator;

    @Excel(name = "操作详情")
    private String details;

    public static ErpOperationLog fromOperationLog(OperationLog log) {
        ErpOperationLog operationLog = new ErpOperationLog();
        operationLog.setOperationTime(log.getOperationTime());
        operationLog.setOperationType(log.getOperationType());
        operationLog.setRecordId(log.getRecordId());
        operationLog.setOperator(log.getOperator());
        operationLog.setDetails(log.getDetails());
        if (operationLog.getRecordId() != null && operationLog.getRecordId().length() > 255) {
            operationLog.setRecordId(operationLog.getRecordId().substring(0, 252) + "...");
        }
        return operationLog;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(Date operationTime) {
        this.operationTime = operationTime;
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
