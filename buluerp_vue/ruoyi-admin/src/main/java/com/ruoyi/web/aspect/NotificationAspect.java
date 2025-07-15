package com.ruoyi.web.aspect;

import com.ruoyi.web.annotation.MarkNotificationsAsRead;
import com.ruoyi.web.service.IErpNotificationService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * 标记通知已读的切面处理类
 */
@Aspect
@Component
public class NotificationAspect {

    private static final Logger log = LoggerFactory.getLogger(NotificationAspect.class);

    @Autowired
    private IErpNotificationService notificationService;

    // 配置织入点
    @Pointcut("@annotation(com.ruoyi.web.annotation.MarkNotificationsAsRead)")
    public void notificationPointCut() {
    }

    @Around("notificationPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        MarkNotificationsAsRead annotation = method.getAnnotation(MarkNotificationsAsRead.class);

        if (annotation != null) {
            try {
                String businessType = annotation.businessType();
                String businessIdsExpression = annotation.businessIdsExpression();

                // 解析SpEL表达式获取businessIds
                ExpressionParser parser = new SpelExpressionParser();
                StandardEvaluationContext context = new StandardEvaluationContext();
                String[] parameterNames = signature.getParameterNames();
                Object[] args = joinPoint.getArgs();
                for (int i = 0; i < parameterNames.length; i++) {
                    context.setVariable(parameterNames[i], args[i]);
                }

                // SpEL表达式需要以'#'开头，但为了用户配置方便，这里兼容不写'#'的情况
                if (!businessIdsExpression.startsWith("#")) {
                    businessIdsExpression = "#" + businessIdsExpression;
                }
                
                Object rawValue = parser.parseExpression(businessIdsExpression).getValue(context);

                if (rawValue instanceof Collection) {
                    Collection<?> businessIds = (Collection<?>) rawValue;
                    if (!businessIds.isEmpty()) {
                        log.info("AOP - Marking notifications as read for businessType: {}, businessIds: {}", businessType, businessIds);
                        for (Object idObj : businessIds) {
                            if (idObj != null) {
                                try {
                                    Long businessId = Long.valueOf(idObj.toString());
                                    notificationService.markNotificationsAsReadByBusiness(businessId,businessType);
                                } catch (NumberFormatException e) {
                                    log.warn("AOP - Could not convert business ID to Long: {}", idObj, e);
                                }
                            }
                        }
                    } else {
                         log.warn("AOP - Business ID collection is empty for businessType: {}. Expression: {}", businessType, businessIdsExpression);
                    }
                } else if (rawValue != null) {
                    // 处理单个值的情况
                    log.info("AOP - Marking notifications as read for businessType: {}, businessId: {}", businessType, rawValue);
                    try {
                        Long businessId = Long.valueOf(rawValue.toString());
                        notificationService.markNotificationsAsReadByBusiness(businessId, businessType);
                    } catch (NumberFormatException e) {
                        log.warn("AOP - Could not convert business ID to Long: {}", rawValue, e);
                    }
                } else {
                    log.warn("AOP - Could not determine businessIds for businessType: {}. Expression returned null. Expression: {}", businessType, businessIdsExpression);
                }
            } catch (Exception e) {
                log.error("AOP - Error while marking notifications as read", e);
                // 即使注解处理失败，也不应该影响主业务流程
            }
        }

        // 执行原方法
        return joinPoint.proceed();
    }
} 