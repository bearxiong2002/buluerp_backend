package com.ruoyi.web.aspect;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.web.util.OperationUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.swagger.annotations.ApiOperation;

import java.lang.reflect.Method;
import java.util.List;

@Aspect
@Component
public class OperationLogAspect {
    private final Logger log = LoggerFactory.getLogger(OperationLogAspect.class);

    // 定义切入点：匹配 com.ruoyi.web.controller.erp 包下所有以 Controller 结尾的类中的方法，并且这些方法带有 @PostMapping 注解
    @Pointcut("execution(* com.ruoyi.web.controller.erp.*Controller.*(..)) &&" +
            "@annotation(io.swagger.annotations.ApiOperation)")
    public void operation() {}

    // 在目标方法执行后记录 @ApiOperation 的 value 值
    @Around("operation()")
    public Object logApiOperationValue(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Object result = joinPoint.proceed();

        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
        String op = apiOperation.value();  // 获取 value 属性
        log.info(
            "{} 在 {} 执行了{}",
            SecurityUtils.getUsername(),
            DateUtils.getTime(),
            op
        );

        List<OperationUtil.UpdateRecord> updateRecords = OperationUtil.getUpdateRecords();
        for (OperationUtil.UpdateRecord updateRecord : updateRecords) {
            if (updateRecord.shouldDisplay()) {
                log.info(updateRecord.display());
            }
        }
        OperationUtil.clearUpdateRecord();
        return result;
    }
}
