package com.ruoyi.web.interceptor;

import com.ruoyi.web.exception.AutoLogException;
import com.ruoyi.web.log.*;
import com.ruoyi.web.mapper.ErpCustomersMapper;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class })
})
public class OperationLogInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        String methodName = mappedStatement.getId();

        OperationLog operationLog = null;
        try {
            boolean autoLog = LogUtil.isAutoLog() &&
                    methodName.startsWith(ErpCustomersMapper.class.getPackage().getName());
            if (autoLog) {
                String className = methodName.substring(0, methodName.lastIndexOf("."));
                String methodSimpleName = methodName.substring(methodName.lastIndexOf(".") + 1);
                Class<?> mapperClass = Class.forName(className);
                Method[] methods = ReflectionUtils.getAllDeclaredMethods(mapperClass);
                Method method = Arrays.stream(methods)
                        .filter(m -> m.getName().equals(methodSimpleName))
                        .findFirst()
                        .orElse(null);
                autoLog = !mapperClass.isAnnotationPresent(AutoLogIgnore.class) &&
                        (method == null || !method.isAnnotationPresent(AutoLogIgnore.class));
            }
            if (autoLog) {
                if (SqlCommandType.UPDATE.equals(mappedStatement.getSqlCommandType())) {
                    operationLog = LogUtil.extractUpdateLog(invocation);
                } else if (SqlCommandType.DELETE.equals(mappedStatement.getSqlCommandType())) {
                    operationLog = LogUtil.extractDeleteLog(invocation);
                } else if (SqlCommandType.INSERT.equals(mappedStatement.getSqlCommandType())) {
                    operationLog = LogUtil.extractInsertLog(invocation);
                }
            }
        } catch (Exception e) {
            throw new AutoLogException("自动提取日志失败：" + e.getMessage(), e);
        }

        Object result = invocation.proceed();
        try {
            if (operationLog != null) {
                if (operationLog instanceof InsertLog) {
                    operationLog = LogUtil.completeInsertLog((InsertLog) operationLog, invocation);
                }
                if (operationLog != null) {
                    LogUtil.addOperationLog(operationLog);
                }
            }
        } catch (Exception e) {
            throw new AutoLogException("自动记录日志失败：" + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可以在这里读取配置属性
    }
}

