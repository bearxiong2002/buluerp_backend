package com.ruoyi.web.util;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.reflect.ReflectUtils;
import com.ruoyi.web.domain.ErpCustomers;
import io.swagger.annotations.ApiModel;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Invocation;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OperationUtil {
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

    public static class UpdateRecord {
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

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        private String operator;
        private String tableName;
        private Long id;
        private List<PropertyChange> changes = new ArrayList<>();

        public boolean shouldDisplay() {
            try {
                return !translateTableName(tableName).isEmpty();
            } catch (ClassNotFoundException e) {
                return false;
            }
        }

        public String display() throws ClassNotFoundException {
            return String.format(
                    "用户 %s 将 %s %d 的 ",
                    operator,
                    translateTableName(tableName),
                    id
            ) + changes.stream()
                    .filter(change -> {
                        try {
                            return !translateFieldName(tableName, change.getName()).isEmpty();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .map(change -> {
                        try {
                            return String.format(
                                    "%s 从 %s 修改为 %s",
                                    translateFieldName(tableName, change.getName()),
                                    change.getOldValue(),
                                    change.getNewValue()
                            );
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.joining(", "));
        }
    }

    private static final ThreadLocal<List<UpdateRecord>> updateRecord = new ThreadLocal<>();

    public static List<UpdateRecord> getUpdateRecords() {
        return updateRecord.get();
    }

    public static void setUpdateRecord(List<UpdateRecord> updateRecord) {
        OperationUtil.updateRecord.set(updateRecord);
    }

    public static void addUpdateRecord(UpdateRecord updateRecord) {
        List<UpdateRecord> updateRecordList = getUpdateRecords();
        if (updateRecordList == null) {
            updateRecordList = new ArrayList<>();
            setUpdateRecord(updateRecordList);
        }
        updateRecordList.add(updateRecord);
    }

    public static void clearUpdateRecord() {
        updateRecord.remove();
    }

    public static String snakeCaseToCamelCase(String str, boolean firstUpperCase) {
        StringBuilder result = new StringBuilder();
        String[] words = str.split("_");
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (word.isEmpty()) {
                continue;
            }
            if (i > 0 || firstUpperCase) {
                result.append(Character.toUpperCase(word.charAt(0)));
                result.append(word.substring(1));
            } else {
                result.append(word);
            }
        }
        return result.toString();
    }

    public static String camelCaseToSnakeCase(String str) {
        StringBuilder result = new StringBuilder();
        // 把第一个字母变成小写
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        str = Character.toLowerCase(str.charAt(0)) + str.substring(1);
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                result.append("_");
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public static String extractTableName(String sql) {
        // 简单提取表名（适用于 UPDATE 表名 SET ...）
        String lowerSql = sql.toLowerCase();
        int updateIndex = lowerSql.indexOf("update");
        int setIndex = lowerSql.indexOf("set");

        if (updateIndex != -1 && setIndex != -1) {
            return sql.substring(updateIndex + 6, setIndex).trim();
        }

        return null;
    }

    public static final Pattern UPDATE_PATTERN
            = Pattern.compile("update\\s+(.+)\\s+set\\s+(.+)\\s+where\\s+(.+);?", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    public static ResultSet selectOldValue(String sql, Long id, Connection conn) throws SQLException {
        // 提取 UPDATE 表名 SET ... WHERE ... 中的旧值
        Matcher matcher = UPDATE_PATTERN.matcher(sql);

        if (matcher.find()) {
            String tableName = matcher.group(1);
            String setClause = matcher.group(2);
            String[] setClauses = Arrays.stream(setClause.split(","))
                    .map(String::trim)
                    .toArray(String[]::new);
            String fields = Arrays.stream(setClauses)
                    .map(clause -> clause.split("=")[0].trim())
                    .collect(Collectors.joining(","));
            String query = String.format("SELECT %s FROM %s WHERE id = %d", fields, tableName, id);
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet;
            }
        }
        return null;
    }

    public static List<OperationUtil.PropertyChange> extractPropertyUpdates(Invocation invocation) throws SQLException {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql();

        List<OperationUtil.PropertyChange> propertyChanges = new ArrayList<>();

        if (parameter == null || parameterMappings == null || parameterMappings.isEmpty()) {
            return propertyChanges;
        }

        Executor executor = (Executor) invocation.getTarget();
        Connection conn = executor.getTransaction().getConnection();
        Long id = ReflectUtils.getFieldValue(parameter, "id");
        ResultSet oldValues = OperationUtil.selectOldValue(sql, id, conn);
        if (oldValues == null) {
            return propertyChanges;
        }

        for (ParameterMapping mapping : parameterMappings) {
            String propertyName = mapping.getProperty();
            String columnName = OperationUtil.camelCaseToSnakeCase(propertyName);
            if ("id".equalsIgnoreCase(propertyName)) {
                continue;
            }
            String oldValue;
            Object newValue;

            oldValue = oldValues.getString(columnName);

            if (parameter instanceof Map) {
                newValue = ((Map<?, ?>) parameter).get(propertyName);
            } else {
                newValue = ReflectUtils.getFieldValue(parameter, propertyName);
            }

            OperationUtil.PropertyChange propertyChange = new OperationUtil.PropertyChange();
            propertyChange.setName(propertyName);
            propertyChange.setOldValue(oldValue);
            propertyChange.setNewValue(newValue);
            propertyChanges.add(propertyChange);
        }

        return propertyChanges;
    }

    public static UpdateRecord extractUpdateRecord(Invocation invocation) throws SQLException {
        UpdateRecord updateRecord = new UpdateRecord();
        Object parameter = invocation.getArgs()[1];
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        String sql = boundSql.getSql();
        updateRecord.setTableName(extractTableName(sql));
        updateRecord.setOperator(SecurityUtils.getUsername());
        updateRecord.setId(ReflectUtils.getFieldValue(parameter, "id"));
        updateRecord.setChanges(extractPropertyUpdates(invocation));
        return updateRecord;
    }

    public static String translateFieldName(String tableName, String fieldName) throws ClassNotFoundException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        String entityClassName = ErpCustomers.class
                .getPackage().getName() + "." + snakeCaseToCamelCase(tableName, true);
        Class<?> clazz = Class.forName(entityClassName);
        Field field = ReflectUtils.getAccessibleField(clazz.newInstance(), snakeCaseToCamelCase(fieldName, false));
        if (field.isAnnotationPresent(Excel.class)) {
            Excel excel = field.getAnnotation(Excel.class);
            return excel.name();
        }
        return "";
    }

    public static String translateTableName(String tableName) throws ClassNotFoundException {
        String entityClassName = ErpCustomers.class
                .getPackage().getName() + "." + snakeCaseToCamelCase(tableName, true);
        Class<?> clazz = Class.forName(entityClassName);
        if (clazz.isAnnotationPresent(ApiModel.class)) {
            return clazz.getAnnotation(ApiModel.class).value();
        }
        return "";
    }
}
