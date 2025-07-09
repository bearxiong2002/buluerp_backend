package com.ruoyi.web.util.log;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class InsertLog implements OperationLog {
    private Date operationTime;
    private String operator;
    private String tableName;
    private List<String> ids;

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setOperationTime(Date operationTime) {
        this.operationTime = operationTime;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public Date getOperationTime() {
        return operationTime;
    }

    @Override
    public String getOperationType() {
        return "新增" + LogUtil.translateTableName(this.tableName);
    }

    @Override
    public String getRecordId() {
        if (ids != null && !ids.isEmpty()) {
            return LogUtil.translateTableName(this.tableName) +
                    ids.stream().map(String::valueOf).collect(Collectors.joining(", "));
        }
        return "";
    }

    @Override
    public String getOperator() {
        return operator;
    }

    @Override
    public String getDetails() {
        String recordId = getRecordId();
        if (recordId.isEmpty()) {
            return "";
        }
        return "新增了" + recordId;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
