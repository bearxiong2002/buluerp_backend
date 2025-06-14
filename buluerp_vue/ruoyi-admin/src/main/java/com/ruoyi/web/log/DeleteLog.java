package com.ruoyi.web.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DeleteLog implements OperationLog {
    public static final String OPERATION_TYPE = "DELETE";

    private List<Long> ids = new ArrayList<>();

    private String tableName;
    private String operator;
    private Date operationTime;

    @Override
    public Date getOperationTime() {
        return operationTime;
    }

    @Override
    public String getOperationType() {
        return OPERATION_TYPE;
    }

    @Override
    public String getOperator() {
        return operator;
    }

    @Override
    public String getDetails() {
        String type = LogUtil.translateTableName(tableName);
        if (type.isEmpty()) {
            return "";
        }
        if (ids == null || ids.isEmpty()) {
            return "";
        }
        return "删除了" + type +
                ids.stream().map(String::valueOf).collect(Collectors.joining(", "));
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public void addId(Long id) {
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
