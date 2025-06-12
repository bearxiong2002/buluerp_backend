package com.ruoyi.web.interceptor;

import com.ruoyi.web.mapper.ErpOperationLogMapper;
import com.ruoyi.web.util.OperationUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class })
})
public class UpdateSqlInterceptor implements Interceptor {
    @Resource
    private ErpOperationLogMapper erpOperationLogMapper;


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];

        // 检查是否是 UPDATE 类型
        if (SqlCommandType.UPDATE.equals(mappedStatement.getSqlCommandType())) {
            OperationUtil.addUpdateRecord(
                    OperationUtil.extractUpdateRecord(invocation)
            );
        }

            // 执行原始方法
        return invocation.proceed();
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

