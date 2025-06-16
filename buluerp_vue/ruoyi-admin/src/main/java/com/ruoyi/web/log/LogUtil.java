package com.ruoyi.web.log;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.reflect.ReflectUtils;
import com.ruoyi.web.domain.ErpCustomers;
import io.swagger.annotations.ApiModel;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Invocation;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LogUtil {

    private static final ThreadLocal<List<OperationLog>> OPETATION_LOG = new ThreadLocal<>();

    public static List<OperationLog> getOperationLog() {
        if (OPETATION_LOG.get() == null) {
            OPETATION_LOG.set(new ArrayList<>());
        }
        return OPETATION_LOG.get();
    }

    public static void setOperationLog(List<OperationLog> opLog) {
        LogUtil.OPETATION_LOG.set(opLog);
    }

    public static void addOperationLog(OperationLog opLog) {
        List<OperationLog> opLogList = getOperationLog();
        opLogList.add(opLog);
    }

    public static void clearOperationLog() {
        OPETATION_LOG.remove();
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

    public static String getCurrentOperator() {
        try {
            return SecurityUtils.getUsername();
        } catch (Exception e) {
            return "系统";
        }
    }

    public static String extractUpdateTableName(String sql) {
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

    public static ResultSet selectBeforeUpdate(String sql, Long id, Connection conn) throws SQLException {
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

    public static List<UpdateLog.PropertyChange> extractPropertyUpdates(Invocation invocation) throws SQLException {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql();

        List<UpdateLog.PropertyChange> propertyChanges = new ArrayList<>();

        if (parameter == null || parameterMappings == null || parameterMappings.isEmpty()) {
            return propertyChanges;
        }

        Executor executor = (Executor) invocation.getTarget();
        Connection conn = executor.getTransaction().getConnection();
        Long id = ReflectUtils.getFieldValue(parameter, "id");
        ResultSet oldValues = LogUtil.selectBeforeUpdate(sql, id, conn);
        if (oldValues == null) {
            return propertyChanges;
        }

        for (ParameterMapping mapping : parameterMappings) {
            String propertyName = mapping.getProperty();
            String columnName = LogUtil.camelCaseToSnakeCase(propertyName);
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

            UpdateLog.PropertyChange propertyChange = new UpdateLog.PropertyChange();
            propertyChange.setName(propertyName);
            propertyChange.setOldValue(oldValue);
            propertyChange.setNewValue(newValue);
            propertyChanges.add(propertyChange);
        }

        return propertyChanges;
    }

    public static UpdateLog extractUpdateLog(Invocation invocation) throws SQLException {
        UpdateLog updateLog = new UpdateLog();
        Object parameter = invocation.getArgs()[1];
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        String sql = boundSql.getSql();
        updateLog.setTableName(extractUpdateTableName(sql));
        updateLog.setOperator(getCurrentOperator());
        updateLog.setOperationTime(DateUtils.getNowDate());
        updateLog.setId(ReflectUtils.getFieldValue(parameter, "id"));
        updateLog.setChanges(extractPropertyUpdates(invocation));
        return updateLog;
    }


    public static final Pattern DELETE_PATTERN
            = Pattern.compile("delete\\s+from\\s+(\\S+)\\s+where\\s+(.+);?", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    public static DeleteLog extractDeleteLog(Invocation invocation) throws SQLException {
        DeleteLog deleteLog = new DeleteLog();
        deleteLog.setOperator(getCurrentOperator());
        deleteLog.setOperationTime(DateUtils.getNowDate());

        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        String sql = boundSql.getSql();

        Matcher matcher = DELETE_PATTERN.matcher(sql);
        if (matcher.find()) {
            Executor executor = (Executor) invocation.getTarget();
            Connection conn = executor.getTransaction().getConnection();
            String tableName = matcher.group(1);
            String conditions = matcher.group(2);
            String query = String.format("SELECT id FROM %s WHERE %s", tableName, conditions);
            PreparedStatement statement = conn.prepareStatement(query);
            ParameterHandler parameterHandler = mappedStatement.getConfiguration()
                    .newParameterHandler(mappedStatement, parameter, boundSql);
            parameterHandler.setParameters(statement);
            ResultSet resultSet = statement.executeQuery();

            deleteLog.setTableName(tableName);
            while (resultSet.next()) {
                deleteLog.addId(resultSet.getLong("id"));
            }
        }
        return deleteLog;
    }

    public static final Pattern INSERT_PATTERN
            = Pattern.compile("insert\\s+into\\s+(\\S+)\\s*\\((.+)\\)\\s+values\\s+(.+);?", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    public static InsertLog extractInsertLog(Invocation invocation) {
        InsertLog insertLog = new InsertLog();
        insertLog.setOperator(getCurrentOperator());
        insertLog.setOperationTime(DateUtils.getNowDate());

        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        String sql = boundSql.getSql();

        Matcher matcher = INSERT_PATTERN.matcher(sql);
        if (matcher.find()) {
            String tableName = matcher.group(1);
            String valuesString = matcher.group(3);
            insertLog.setTableName(tableName);
            insertLog.setCount(valuesString.split("\\(").length - 1);
        }

        return insertLog;
    }

    public static String translateFieldName(String tableName, String fieldName) {
        try {
            String entityClassName = ErpCustomers.class
                    .getPackage().getName() + "." + snakeCaseToCamelCase(tableName, true);
            Class<?> clazz = Class.forName(entityClassName);
            Field field = ReflectUtils.getAccessibleField(clazz, snakeCaseToCamelCase(fieldName, false));
            if (field.isAnnotationPresent(Excel.class)) {
                Excel excel = field.getAnnotation(Excel.class);
                return excel.name();
            }
        } catch (ClassNotFoundException e) {
            // 忽略
        }
        return "";
    }

    public static String translateTableName(String tableName) {
        try {
            String entityClassName = ErpCustomers.class
                    .getPackage().getName() + "." + snakeCaseToCamelCase(tableName, true);
            Class<?> clazz = Class.forName(entityClassName);
            if (clazz.isAnnotationPresent(ApiModel.class)) {
                return clazz.getAnnotation(ApiModel.class).value();
            }
        } catch (ClassNotFoundException e) {
            // 忽略
        }
        return "";
    }
}
