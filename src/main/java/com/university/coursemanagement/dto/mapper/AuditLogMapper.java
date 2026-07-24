package com.university.coursemanagement.dto.mapper;

import com.university.coursemanagement.dto.response.AuditLogResponse;
import com.university.coursemanagement.entity.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {
    public AuditLogResponse toResponse(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getEntityName(),
                log.getEntityId(),
                log.getAction(),
                log.getActor(),
                log.getDetail(),
                log.getCreatedAt()
        );
    }
}
