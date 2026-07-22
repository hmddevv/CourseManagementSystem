package com.university.coursemanagement.dto.response;

import com.university.coursemanagement.entity.enums.AuditAction;

import java.time.LocalDateTime;

/** Mot dong nhat ky thao tac tra ve cho client. */
public record AuditLogResponse(
        Long id,
        String entityName,
        Long entityId,
        AuditAction action,
        String actor,
        String detail,
        LocalDateTime createdAt
) {
}
