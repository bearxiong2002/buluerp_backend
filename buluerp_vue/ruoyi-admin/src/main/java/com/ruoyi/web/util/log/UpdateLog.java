package com.ruoyi.web.util.log;

import java.util.*;
import java.util.stream.Collectors;

public class UpdateLog implements OperationLog {
    public static final String[] IGNORE_FIELDS = new String[]{"^operator", "^updateTime$"};

    public static boolean isIgnoreField(String fieldName) {
        for (String ignoreField : IGNORE_FIELDS) {
            if (fieldName.matches(ignoreField)) {
                return true;
            }
        }
        return false;
    }

    public String getTableName() {
        return tableName;
    }

    public Map<String, List<PropertyChange>> getChanges() {
        return changes;
    }

    public void setChanges(Map<String, List<PropertyChange>> changes) {
        this.changes = changes;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
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

    @Override
    public String getRecordId() {
        return LogUtil.translateTableName(tableName) +
                String.join(", ", changes.keySet());
    }

    private Date operationTime;
    private String tableName;
    private Map<String, List<PropertyChange>> changes = new HashMap<>();

    @Override
    public String getOperationType() {
        return "更新" + LogUtil.translateTableName(tableName);
    }

    @Override
    public String getDetails() {
        return changes.entrySet().stream()
                .map(change -> {
                    String id = change.getKey();
                    String updates = change.getValue().stream()
                            .filter(fieldChange -> !UpdateLog.isIgnoreField(fieldChange.getName()))
                            .filter(fieldChange -> !Objects.equals(
                                    Objects.toString(fieldChange.getOldValue()),
                                    Objects.toString(fieldChange.getNewValue())
                            ))
                            .map(fieldChange -> String.format(
                                    "%s从%s改为%s",
                                    LogUtil.translateFieldName(tableName, fieldChange.getName()),
                                    LogUtil.formatValue(fieldChange.getOldValue()),
                                    LogUtil.formatValue(fieldChange.getNewValue())
                            ))
                            .collect(Collectors.joining(", "));
                    if (updates.isEmpty()) {
                        return "";
                    }
                    return String.format(
                            "将%s%s的%s",
                            LogUtil.translateTableName(tableName),
                            id,
                            updates
                    );
                })
                .collect(Collectors.joining(", "));
    }

    public static class PropertyChange {
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getOldValue() {
            return oldValue;
        }

        public void setOldValue(Object oldValue) {
            this.oldValue = oldValue;
        }

        public Object getNewValue() {
            return newValue;
        }

        public void setNewValue(Object newValue) {
            this.newValue = newValue;
        }

        private String name;
        private Object oldValue;
        private Object newValue;
    }
}
