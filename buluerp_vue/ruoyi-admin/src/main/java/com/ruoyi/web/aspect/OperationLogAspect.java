package com.ruoyi.web.aspect;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.web.log.AutoLogIgnore;
import com.ruoyi.web.log.LogUtil;
import com.ruoyi.web.log.OperationLog;
import com.ruoyi.web.service.IErpOperationLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.List;

@Aspect
@Component
public class OperationLogAspect {
    private final Logger log = LoggerFactory.getLogger(OperationLogAspect.class);

    @Resource
    private IErpOperationLogService erpOperationLogService;

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

            List<OperationLog> opLogs = LogUtil.getOperationLog();
            for (OperationLog opLog : opLogs) {
                debugOperation(opLog);
            }
            erpOperationLogService.saveOperations(opLogs);

            return result;
        } finally {
            LogUtil.clearOperationLog();
            LogUtil.resetCurrentOperator();
            LogUtil.resetUserOperating();
        }
    }

    public void debugOperation(OperationLog opLog) {
        log.debug("==================== 操作日志 ====================");
        log.debug("操作时间:\t{}", DateUtils.getTime());
        log.debug("操作人:\t{}", LogUtil.getCurrentOperator());
        log.debug("操作类型:\t{}", opLog.getOperationType());
        log.debug("操作记录ID:\t{}", opLog.getRecordId());
        log.debug("操作详情:\t{}", opLog.getDetails());
        log.debug("==================================================");
    }
}
