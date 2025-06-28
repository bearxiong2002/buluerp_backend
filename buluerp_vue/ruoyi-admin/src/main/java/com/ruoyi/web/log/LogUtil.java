package com.ruoyi.web.log;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.reflect.ReflectUtils;
import com.ruoyi.web.annotation.LogIdentifier;
import com.ruoyi.web.domain.ErpCustomers;
import io.swagger.annotations.ApiModel;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogUtil {

    private static final ThreadLocal<List<OperationLog>> OPETATION_LOG = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> IS_AUTOLOG = new ThreadLocal<>();

    public static List<OperationLog> getOperationLog() {
        if (OPETATION_LOG.get() == null) {
            OPETATION_LOG.set(new ArrayList<>());
        }
        return OPETATION_LOG.get();
    }

    public static boolean isAutoLog() {
        if (IS_AUTOLOG.get() == null) {
            IS_AUTOLOG.set(true);
        }
        return IS_AUTOLOG.get();
    }

    public static void setAutoLog(boolean isAutoLog) {
        IS_AUTOLOG.set(isAutoLog);
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

    public static String getIdentifierFieldName(String tableName) {
        Class<?> clazz = getClassByTableName(tableName);
        if (clazz == null) {
            return "id";
        }
        while (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(LogIdentifier.class)) {
                    return field.getName();
                }
            }
            clazz = clazz.getSuperclass();
        }
        return "id";
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

    public static ResultSet selectOldValuesBeforeUpdate(Executor executor, MappedStatement originalStatement, Object parameter) throws SQLException {
        // 提取 UPDATE 表名 SET ... WHERE ... 中的旧值
        BoundSql originalBoundSql = originalStatement.getBoundSql(parameter);
        String originalSql = originalBoundSql.getSql();
        Matcher matcher = UPDATE_PATTERN.matcher(originalSql);

        if (matcher.find()) {
            String tableName = matcher.group(1).trim();
            String setClause = matcher.group(2);
            String[] setClauses = Arrays.stream(setClause.split(","))
                    .map(String::trim)
                    .toArray(String[]::new);
            String[] fields = Arrays.stream(setClauses)
                    .map(clause -> clause.split("=")[0].trim())
                    .toArray(String[]::new);
            String fieldsString = String.join(",", fields);
            String identifierColumnName = camelCaseToSnakeCase(
                    getIdentifierFieldName(tableName)
            );
            if (Arrays.stream(fields).noneMatch(field -> field.equals(identifierColumnName) || field.endsWith("." + identifierColumnName))) {
                fieldsString = fieldsString + "," + tableName + "." + identifierColumnName;
            }
            long setParamsCount = Arrays.stream(setClauses)
                    .map(clause -> clause.split("=")[1].trim())
                    .filter(param -> param.equals("?"))
                    .count();
            StringBuilder extraParam = new StringBuilder("1");
            for (int i = 0; i < setParamsCount; i++) {
                extraParam.append(" OR ? is null");
            }
            String whereClause = matcher.group(3);
            String selectSql = String.format(
                    "select %s from %s where (%s) and (%s)",
                    fieldsString,
                    tableName,
                    extraParam,
                    whereClause
            );
            Connection connection = executor.getTransaction().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(selectSql);
            ParameterHandler parameterHandler = originalStatement.getConfiguration()
                    .newParameterHandler(originalStatement, parameter, originalBoundSql);
            parameterHandler.setParameters(preparedStatement);
            return preparedStatement.executeQuery();
        }
        return null;
    }

    public static Object getValueByPath(Object obj, String path) {
        String[] paths = path.split("\\.");
        Object result = obj;
        for (String pathPart : paths) {
            if (result == null) {
                return null;
            }
            if (result instanceof Map) {
                result = ((Map<?, ?>) result).get(pathPart);
            } else {
                result = ReflectUtils.getFieldValue(result, pathPart);
            }
        }
        return result;
    }

    public static Map<String, List<UpdateLog.PropertyChange>> extractUpdateChanges(Invocation invocation) throws SQLException {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql();
        String tableName = extractUpdateTableName(sql);
        String identifierFieldName = getIdentifierFieldName(tableName);

        Map<String, List<UpdateLog.PropertyChange>> changes = new HashMap<>();

        if (parameterMappings == null || parameterMappings.isEmpty()) {
            return changes;
        }

        Executor executor = (Executor) invocation.getTarget();
        try (ResultSet oldValues = selectOldValuesBeforeUpdate(executor, mappedStatement, parameter)) {
            if (oldValues == null) {
                return changes;
            }
            ResultSetMetaData metaData = oldValues.getMetaData();

            while (oldValues.next()) {
                String identifierValue = oldValues.getString(camelCaseToSnakeCase(identifierFieldName));
                List<UpdateLog.PropertyChange> propertyChanges = new ArrayList<>();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String fieldName = snakeCaseToCamelCase(metaData.getColumnName(i), false);
                    if (identifierFieldName.equals(fieldName)) {
                        continue;
                    }
                    String translatedFieldName = translateFieldName(tableName, fieldName);
                    if (translatedFieldName.isEmpty()) {
                        continue;
                    }
                    UpdateLog.PropertyChange propertyChange = new UpdateLog.PropertyChange();
                    propertyChange.setName(translatedFieldName);
                    propertyChange.setOldValue(oldValues.getObject(i));
                    parameterMappings.stream()
                            .filter(mapping -> mapping.getProperty().equals(fieldName) || mapping.getProperty().endsWith("." + fieldName))
                            .findFirst()
                            .ifPresent(fieldMapping -> propertyChange.setNewValue(getValueByPath(parameter, fieldMapping.getProperty())));
                    propertyChanges.add(propertyChange);
                }
                if (propertyChanges.isEmpty()) {
                    continue;
                }
                changes.put(identifierValue, propertyChanges);
            }

            return changes;
        }
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
        updateLog.setChanges(extractUpdateChanges(invocation));
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
            String tableName = matcher.group(1).trim();
            String conditions = matcher.group(2);
            String identifierFieldName = getIdentifierFieldName(tableName);
            String identifierColumnName = camelCaseToSnakeCase(identifierFieldName);
            deleteLog.setTableName(tableName);

            String query = String.format("SELECT %s.%s FROM %s WHERE %s", tableName, identifierColumnName, tableName, conditions);
            Connection conn = executor.getTransaction().getConnection();
            PreparedStatement statement = conn.prepareStatement(query);
            ParameterHandler parameterHandler = mappedStatement.getConfiguration()
                    .newParameterHandler(mappedStatement, parameter, boundSql);
            parameterHandler.setParameters(statement);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String identifierValue = resultSet.getString(identifierColumnName);
                    deleteLog.addId(identifierValue);
                }
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

    public static Class<?> getClassByTableName(String tableName) {
        try {
            String entityClassName = ErpCustomers.class
                    .getPackage().getName() + "." + snakeCaseToCamelCase(tableName, true);
            return Class.forName(entityClassName);
        } catch (ClassNotFoundException e) {
            return null;
        }
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
