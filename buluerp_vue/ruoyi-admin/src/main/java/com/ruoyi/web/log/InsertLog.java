package com.ruoyi.web.log;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class InsertLog implements OperationLog {
    public static final String OPERATION_TYPE = "INSERT";

    private Date operationTime;
    private String operator;
    private String tableName;
    private List<String> columns;
    private List<List<String>> values;

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
    public String getOperator() {
        return operator;
    }

    @Override
    public String getDetails() {
        String type = LogUtil.translateTableName(tableName);
        if (type.isEmpty()) {
            return "";
        }
        if (columns == null || values == null) {
            return "";
        }
        return "新建了" + values.size() + "条" + type + "记录";
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<List<String>> getValues() {
        return values;
    }

    public void setValues(List<List<String>> values) {
        this.values = values;
    }
}
