package com.ruoyi.web.interceptor;

import com.ruoyi.web.log.DeleteLog;
import com.ruoyi.web.log.OperationLog;
import com.ruoyi.web.log.UpdateLog;
import com.ruoyi.web.mapper.ErpCustomersMapper;
import com.ruoyi.web.log.LogUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;

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
        if (methodName.startsWith(ErpCustomersMapper.class.getPackage().getName())) {
            if (SqlCommandType.UPDATE.equals(mappedStatement.getSqlCommandType())) {
                operationLog = LogUtil.extractUpdateLog(invocation);
            } else if (SqlCommandType.DELETE.equals(mappedStatement.getSqlCommandType())) {
                operationLog = LogUtil.extractDeleteLog(invocation);
            } else if (SqlCommandType.INSERT.equals(mappedStatement.getSqlCommandType())) {
                operationLog = LogUtil.extractInsertLog(invocation);
            }
        }

        Object result = invocation.proceed();
        if (operationLog != null) {
            LogUtil.addOperationLog(operationLog);
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

