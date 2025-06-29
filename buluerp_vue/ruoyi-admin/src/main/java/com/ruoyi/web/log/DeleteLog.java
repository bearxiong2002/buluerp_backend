package com.ruoyi.web.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DeleteLog implements OperationLog {
    private List<String> ids = new ArrayList<>();

    private String tableName;
    private String operator;
    private Date operationTime;

    @Override
    public Date getOperationTime() {
        return operationTime;
    }

    @Override
    public String getOperationType() {
        return "删除" + LogUtil.translateTableName(tableName);
    }

    @Override
    public String getRecordId() {
        return LogUtil.translateTableName(tableName) +
                ids.stream().map(String::valueOf).collect(Collectors.joining(", "));
    }

    @Override
    public String getOperator() {
        return operator;
    }

    @Override
    public String getDetails() {
        if (ids == null || ids.isEmpty()) {
            return "";
        }
        return "删除了" + LogUtil.translateTableName(tableName) +
                ids.stream().map(String::valueOf).collect(Collectors.joining(", "));
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public void addId(String id) {
        this.ids.add(id);
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setOperationTime(Date operationTime) {
        this.operationTime = operationTime;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
