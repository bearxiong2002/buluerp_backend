package com.ruoyi.web.aspect;

import com.ruoyi.web.annotation.AutoLogIgnore;
import com.ruoyi.web.util.log.LogUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class OperationLogAspect {
    private final Logger log = LoggerFactory.getLogger(OperationLogAspect.class);

    // 定义切入点：匹配 com.ruoyi.web.controller.erp 包下所有以 Controller 结尾的类中的方法，
    // 并且这些方法带有 @ApiOperation 注解
    @Pointcut("execution(* com.ruoyi.web.controller.erp.*Controller.*(..)) &&" +
            "@annotation(io.swagger.annotations.ApiOperation)")
    public void operation() {}

    // 在目标方法执行后记录 @ApiOperation 的 value 值
    @Around("operation()")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        LogUtil.setUserOperating(true);
        LogUtil.clearOperationLog();
        if (method.isAnnotationPresent(AutoLogIgnore.class) || method.getDeclaringClass().isAnnotationPresent(AutoLogIgnore.class)) {
            LogUtil.setAutoLog(false);
        }
        try {
            Object result = joinPoint.proceed();
            LogUtil.commitOperationLogs();
            return result;
        } finally {
            LogUtil.clearOperationLog();
            LogUtil.resetCurrentOperator();
            LogUtil.resetUserOperating();
        }
    }
}
