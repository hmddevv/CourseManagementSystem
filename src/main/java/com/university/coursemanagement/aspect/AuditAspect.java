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

/**
 * ASPECT-ORIENTED PROGRAMMING.
 *
 * <p>Ghi nhat ky thao tac ma khong phai chen mot dong code nao vao tang Service.
 * Spring tao mot proxy quanh cac bean {@code *ServiceImpl}; khi phuong thuc ghi
 * chay xong ma khong nem exception, {@code @AfterReturning} duoc kich hoat.</p>
 *
 * <p>{@code @Order(1)} dat aspect nay NGOAI proxy giao dich (proxy giao dich co
 * do uu tien thap nhat), nen nhat ky chi duoc ghi sau khi giao dich nghiep vu da
 * commit thanh cong - khong ghi nham thao tac bi cuon nguoc.</p>
 *
 * <p>Chi audit thao tac GHI. Audit ca thao tac doc se lam phinh bang nhat ky va
 * lam cham moi request danh sach.</p>
 */
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

    /** Moi phuong thuc public cua tang service.impl, tru chinh service ghi nhat ky. */
    @Pointcut("execution(public * com.university.coursemanagement.service.impl.*ServiceImpl.*(..)) "
            + "&& !within(com.university.coursemanagement.service.impl.AuditLogServiceImpl)")
    public void serviceLayer() {
    }

    /** Chi cac phuong thuc lam thay doi du lieu. */
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
            // Ghi nhat ky that bai khong duoc lam hong nghiep vu da thanh cong
            log.warn("Khong ghi duoc nhat ky thao tac: {}", e.getMessage());
        }
    }

    /** "CourseServiceImpl" -> "Course". */
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

    /**
     * Lay id cua ban ghi bi tac dong: uu tien {@code id()} tren DTO tra ve
     * (record response), neu khong co thi lay tham so Long dau tien.
     */
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
            return null;    // DTO khong co id() - khong phai loi
        }
    }

    private String summarizeArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "";
        }
        String text = Arrays.stream(args).map(this::describeArg).collect(Collectors.joining(", "));
        return text.length() <= MAX_ARGS_LENGTH ? text : text.substring(0, MAX_ARGS_LENGTH) + "...";
    }

    /**
     * Entity khong ghi de toString nen mac dinh in ra dia chi bam
     * (vi du {@code Enrollment@2369e44a}) - vo nghia trong nhat ky.
     * Rut gon thanh {@code Enrollment#5}.
     */
    private String describeArg(Object arg) {
        if (arg instanceof BaseEntity entity) {
            return "%s#%s".formatted(entity.getClass().getSimpleName(), entity.getId());
        }
        return String.valueOf(arg);
    }
}
