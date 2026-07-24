package com.university.coursemanagement.aspect;

import com.university.coursemanagement.config.JpaAuditingConfig;
import com.university.coursemanagement.entity.BaseEntity;
import com.university.coursemanagement.entity.enums.AuditAction;
import com.university.coursemanagement.service.AuditLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@Order(1)
public class AuditAspect {
    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    private static final String SERVICE_SUFFIX = "ServiceImpl";
    private static final int MAX_ARGS_LENGTH = 200;

    private final AuditLogService auditLogService;

    public AuditAspect(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Pointcut("execution(public * com.university.coursemanagement.service.impl.*ServiceImpl.*(..)) "
            + "&& !within(com.university.coursemanagement.service.impl.AuditLogServiceImpl)")
    public void serviceLayer() {
    }

    @Pointcut("execution(* create*(..)) || execution(* update*(..)) || execution(* delete*(..)) "
            + "|| execution(* publish*(..)) || execution(* archive*(..)) "
            + "|| execution(* enroll*(..)) || execution(* cancel*(..)) || execution(* issue*(..))")
    public void writeOperation() {
    }

    @AfterReturning(pointcut = "serviceLayer() && writeOperation()", returning = "result")
    public void recordWrite(JoinPoint joinPoint, Object result) {
        try {
            String methodName = joinPoint.getSignature().getName();
            AuditAction action = resolveAction(methodName);
            if (action == null) {
                return;
            }
            auditLogService.record(
                    resolveEntityName(joinPoint),
                    resolveEntityId(joinPoint, result),
                    action,
                    JpaAuditingConfig.currentActor(),
                    "%s(%s)".formatted(methodName, summarizeArgs(joinPoint.getArgs())));
        } catch (RuntimeException e) {
            log.warn("Không ghi được nhật ký thao tác: {}", e.getMessage());
        }
    }

    private String resolveEntityName(JoinPoint joinPoint) {
        String simpleName = joinPoint.getTarget().getClass().getSimpleName();
        int suffix = simpleName.indexOf(SERVICE_SUFFIX);
        return suffix > 0 ? simpleName.substring(0, suffix) : simpleName;
    }

    private AuditAction resolveAction(String methodName) {
        if (methodName.startsWith("create")) return AuditAction.CREATE;
        if (methodName.startsWith("update")) return AuditAction.UPDATE;
        if (methodName.startsWith("delete")) return AuditAction.DELETE;
        if (methodName.startsWith("publish")) return AuditAction.PUBLISH;
        if (methodName.startsWith("archive")) return AuditAction.ARCHIVE;
        if (methodName.startsWith("enroll")) return AuditAction.ENROLL;
        if (methodName.startsWith("cancel")) return AuditAction.CANCEL;
        if (methodName.startsWith("issue")) return AuditAction.CREATE;
        return null;
    }

    private Long resolveEntityId(JoinPoint joinPoint, Object result) {
        Long fromResult = readRecordId(result);
        if (fromResult != null) {
            return fromResult;
        }
        return Arrays.stream(joinPoint.getArgs())
                .filter(Long.class::isInstance)
                .map(Long.class::cast)
                .findFirst()
                .orElse(null);
    }

    private Long readRecordId(Object result) {
        if (result == null) {
            return null;
        }
        try {
            Method idMethod = result.getClass().getMethod("id");
            Object value = idMethod.invoke(result);
            return value instanceof Long id ? id : null;
        } catch (ReflectiveOperationException | SecurityException e) {
            return null;
        }
    }

    private String summarizeArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "";
        }
        String text = Arrays.stream(args).map(this::describeArg).collect(Collectors.joining(", "));
        return text.length() <= MAX_ARGS_LENGTH ? text : text.substring(0, MAX_ARGS_LENGTH) + "...";
    }

    private String describeArg(Object arg) {
        if (arg instanceof BaseEntity entity) {
            return "%s#%s".formatted(entity.getClass().getSimpleName(), entity.getId());
        }
        return String.valueOf(arg);
    }
}
