package com.ruoyi.web.util.log;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.reflect.ReflectUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.web.annotation.AutoLogIgnore;
import com.ruoyi.web.annotation.AutoLogIdentifier;
import com.ruoyi.web.domain.ErpCustomers;
import com.ruoyi.web.service.IErpOperationLogService;
import io.swagger.annotations.ApiModel;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class LogUtil {

    private static final ThreadLocal<List<OperationLog>> OPETATION_LOG = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> IS_AUTOLOG = new ThreadLocal<>();

    private static final Logger logger = LoggerFactory.getLogger(LogUtil.class);

    public static void debugOperation(OperationLog opLog) {
        logger.debug("==================== 操作日志 ====================");
        logger.debug("操作时间:\t{}", DateUtils.getTime());
        logger.debug("操作人:\t{}", LogUtil.getCurrentOperator());
        logger.debug("操作类型:\t{}", opLog.getOperationType());
        logger.debug("操作记录ID:\t{}", opLog.getRecordId());
        logger.debug("操作详情:\t{}", opLog.getDetails());
        logger.debug("==================================================");
    }

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

    private static IErpOperationLogService erpOperationLogService = null;

    // Controller 层方法成功结束后自动调用，其它情况需要手动调用
    public static void commitOperationLogs() {
        if (erpOperationLogService == null) {
            erpOperationLogService = SpringUtils.getBean(IErpOperationLogService.class);
        }
        List<OperationLog> operationLog = getOperationLog();
        for (OperationLog log : operationLog) {
            debugOperation(log);
        }
        erpOperationLogService.saveOperations(operationLog);
        LogUtil.clearOperationLog();
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

    public static final ThreadLocal<Boolean> USER_OPERATING = new ThreadLocal<>();

    // 当前是否为用户操作（即是否为 Controller 层方法调用过程）
    public static boolean isUserOperating() {
        if (USER_OPERATING.get() == null) {
            return false;
        }
        return USER_OPERATING.get();
    }

    public static void setUserOperating(boolean isUserOperating) {
        USER_OPERATING.set(isUserOperating);
    }

    public static void resetUserOperating() {
        USER_OPERATING.remove();
    }

    private static final ThreadLocal<String> CURRENT_OPERATOR = new ThreadLocal<>();

    // 修改当前线程中自动日志的操作人
    public static void setCurrentOperator(String operator) {
        CURRENT_OPERATOR.set(operator);
    }

    public static void setSystemOperator() {
        setCurrentOperator(OperationLog.OPERATOR_SYSTEM);
    }

    public static String getCurrentOperator() {
        if (CURRENT_OPERATOR.get() != null) {
            return CURRENT_OPERATOR.get();
        }
        if (!isUserOperating()) {
            return OperationLog.OPERATOR_SYSTEM;
        }
        try {
            return SecurityUtils.getUsername();
        } catch (Exception e) {
            return OperationLog.OPERATOR_UNKNOWN;
        }
    }

    public static void resetCurrentOperator() {
        CURRENT_OPERATOR.remove();
    }

    public static String getIdentifierFieldName(String tableName) {
        Class<?> clazz = getClassByTableName(tableName);
        if (clazz == null) {
            return "id";
        }
        while (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(AutoLogIdentifier.class)) {
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

    public static ResultSet selectOldValuesBeforeUpdate(Executor executor, MappedStatement originalStatement, Object parameter, List<String> setClausesRecv) throws SQLException {
        // 提取 UPDATE 表名 SET ... WHERE ... 中的旧值
        BoundSql originalBoundSql = originalStatement.getBoundSql(parameter);
        String originalSql = originalBoundSql.getSql();
        Matcher matcher = UPDATE_PATTERN.matcher(originalSql);

        if (matcher.find()) {
            String tableName = matcher.group(1).trim();
            String setClause = matcher.group(2);
            List<String> setClauses = Arrays.stream(setClause.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
            if (setClausesRecv != null) {
                setClausesRecv.addAll(setClauses);
            }
            List<String> fields = setClauses.stream()
                    .map(clause -> clause.split("=")[0].trim())
                    .collect(Collectors.toList());
            String fieldsString = String.join(",", fields);
            String identifierColumnName = camelCaseToSnakeCase(
                    getIdentifierFieldName(tableName)
            );
            if (fields.stream().noneMatch(field -> field.equals(identifierColumnName) || field.endsWith("." + identifierColumnName))) {
                fieldsString = fieldsString + "," + tableName + "." + identifierColumnName;
            }
            long setParamsCount = setClauses.stream()
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
        Class<?> clazz = getClassByTableName(tableName);

        Map<String, List<UpdateLog.PropertyChange>> changes = new HashMap<>();

        if (parameterMappings == null || parameterMappings.isEmpty()) {
            return changes;
        }

        Executor executor = (Executor) invocation.getTarget();
        List<String> setClauses = new ArrayList<>();
        try (ResultSet oldValues = selectOldValuesBeforeUpdate(executor, mappedStatement, parameter, setClauses)) {
            if (oldValues == null) {
                return changes;
            }
            ResultSetMetaData metaData = oldValues.getMetaData();

            Map<String, Object> newValues = new HashMap<>();
            int paramIndex = 0;
            for (String setClause : setClauses) {
                String[] setClauseParts = setClause.split("=");
                if (setClauseParts.length == 2) {
                    String columnName = setClauseParts[0].trim();
                    if (clazz != null) {
                        Field field = ReflectUtils.getAccessibleField(clazz, snakeCaseToCamelCase(columnName, false));
                        if (field != null && field.isAnnotationPresent(AutoLogIgnore.class)) {
                            continue;
                        }
                    }
                    String value = setClauseParts[1].trim();
                    if (value.startsWith("?")) {
                        ParameterMapping parameterMapping = parameterMappings.get(paramIndex++);
                        newValues.put(columnName, getValueByPath(parameter, parameterMapping.getProperty()));
                    } else {
                        newValues.put(columnName, value);
                    }
                }
            }
            while (oldValues.next()) {
                String identifierValue = oldValues.getString(camelCaseToSnakeCase(identifierFieldName));
                List<UpdateLog.PropertyChange> propertyChanges = new ArrayList<>();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String columnName = metaData.getColumnName(i);
                    String fieldName = snakeCaseToCamelCase(columnName, false);
                    if (identifierFieldName.equals(fieldName)) {
                        continue;
                    }
                    String translatedFieldName = translateFieldName(tableName, fieldName);
                    if (!isTranslationSuccess(translatedFieldName)) {
                        continue;
                    }
                    UpdateLog.PropertyChange propertyChange = new UpdateLog.PropertyChange();
                    propertyChange.setName(fieldName);
                    propertyChange.setOldValue(oldValues.getObject(i));
                    if (newValues.containsKey(columnName)) {
                        propertyChange.setNewValue(newValues.get(columnName));
                    } else {
                        continue;
                    }
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
        if (updateLog.getDetails().isEmpty()) {
            return null;
        }
        return updateLog;
    }

    public static InsertLog completeInsertLog(InsertLog insertLog, Invocation invocation) {
        String identifierFieldName = getIdentifierFieldName(insertLog.getTableName());
        Object parameter = invocation.getArgs()[1];
        if (parameter instanceof MapperMethod.ParamMap) {
            // 从参数列表找到第一个非基本类型的参数
            MapperMethod.ParamMap<?> paramMap = (MapperMethod.ParamMap<?>) parameter;
            String firstParamName = paramMap.keySet()
                    .stream()
                    .filter(key -> {
                        Object value = paramMap.get(key);
                        if (value instanceof List || value instanceof Map || value.getClass().isArray()) {
                            return true;
                        }
                        String className = value.getClass().getName();
                        return !className.startsWith("java.lang.") && !className.startsWith("java.util.");
                    })
                    .findFirst()
                    .orElse(null);
            if (firstParamName == null) {
                return null;
            }
            parameter = paramMap.get(firstParamName);
        }
        if (parameter instanceof List || parameter.getClass().isArray()) {
            Stream<?> stream;
            if (parameter instanceof List) {
                stream = ((List<?>) parameter).stream();
            } else {
                stream = Arrays.stream((Object[]) parameter);
            }
            insertLog.setIds(
                    stream
                        .map(item -> getValueByPath(item, identifierFieldName))
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .collect(Collectors.toList())
            );
        } else {
            Object id = getValueByPath(parameter, identifierFieldName);
            if (id != null) {
                insertLog.setIds(Collections.singletonList(id.toString()));
            }
        }
        if (insertLog.getIds() == null || insertLog.getIds().isEmpty()) {
            return null;
        }
        return insertLog;
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
        if (deleteLog.getIds().isEmpty()) {
            return null;
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
            insertLog.setTableName(tableName);
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
        return "<" + fieldName + ">";
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
        return "<" + tableName + ">";
    }

    public static boolean isTranslationSuccess(String nameCn) {
        return !nameCn.startsWith("<") || !nameCn.endsWith(">");
    }

    public static String formatValue(Object value) {
        if (value == null) {
            return "空值";
        } else if (value instanceof String) {
            return "\"" + value + "\"";
        } else if (value instanceof Date) {
            return DateFormatUtils.format((Date) value, "yyyy-MM-dd HH:mm:ss");
        } else if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        return value.toString();
    }
}
