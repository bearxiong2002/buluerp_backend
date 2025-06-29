package com.ruoyi.web.log;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class InsertLog implements OperationLog {
    public static final String OPERATION_TYPE = "INSERT";

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
        return OPERATION_TYPE;
    }

    @Override
    public String getRecordId() {
        String tableName = LogUtil.translateTableName(this.tableName);
        if (!tableName.isEmpty() && ids != null && !ids.isEmpty()) {
            return tableName +
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
        return "新建了" + recordId;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
