package com.ruoyi.web.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateLog implements OperationLog {
    public static final String OPERATION_TYPE = "UPDATE";

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<PropertyChange> getChanges() {
        return changes;
    }

    public void setChanges(List<PropertyChange> changes) {
        this.changes = changes;
    }

    public void addChange(PropertyChange change) {
        this.changes.add(change);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    private String operator;

    @Override
    public Date getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(Date operationTime) {
        this.operationTime = operationTime;
    }

    private Date operationTime;
    private String tableName;
    private Long id;
    private List<PropertyChange> changes = new ArrayList<>();

    @Override
    public String getOperationType() {
        return OPERATION_TYPE;
    }

    @Override
    public String getDetails() {
        String type = LogUtil.translateTableName(tableName);
        if (type.isEmpty()) {
            return "";
        }
        String updates = changes.stream()
                .filter(change -> !LogUtil.translateFieldName(tableName, change.getName()).isEmpty())
                .map(change -> String.format(
                        "%s从\"%s\"修改为\"%s\"",
                        LogUtil.translateFieldName(tableName, change.getName()),
                        change.getOldValue(),
                        change.getNewValue()
                ))
                .collect(Collectors.joining(", "));
        if (updates.isEmpty()) {
            return "";
        }
        return String.format("将%s%d的", type, id) + updates;
    }

    public static class PropertyChange {
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOldValue() {
            return oldValue;
        }

        public void setOldValue(String oldValue) {
            this.oldValue = oldValue;
        }

        public Object getNewValue() {
            return newValue;
        }

        public void setNewValue(Object newValue) {
            this.newValue = newValue;
        }

        private String name;
        private String oldValue;
        private Object newValue;
    }
}
