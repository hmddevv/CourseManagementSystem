package com.university.coursemanagement.service.impl;

import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.mapper.AuditLogMapper;
import com.university.coursemanagement.dto.response.AuditLogResponse;
import com.university.coursemanagement.entity.AuditLog;
import com.university.coursemanagement.entity.enums.AuditAction;
import com.university.coursemanagement.repository.AuditLogRepository;
import com.university.coursemanagement.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuditLogServiceImpl implements AuditLogService {
    private static final int MAX_DETAIL_LENGTH = 500;

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository, AuditLogMapper auditLogMapper) {
        this.auditLogRepository = auditLogRepository;
        this.auditLogMapper = auditLogMapper;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String entityName, Long entityId, AuditAction action, String actor, String detail) {
        auditLogRepository.save(AuditLog.builder()
                .entityName(entityName)
                .entityId(entityId)
                .action(action)
                .actor(actor)
                .detail(truncate(detail))
                .build());
    }

    @Override
    public PageResponse<AuditLogResponse> search(String entityName, AuditAction action, Pageable pageable) {
        Page<AuditLog> page;
        if (entityName != null && action != null) {
            page = auditLogRepository.findByEntityNameAndAction(entityName, action, pageable);
        } else if (entityName != null) {
            page = auditLogRepository.findByEntityName(entityName, pageable);
        } else if (action != null) {
            page = auditLogRepository.findByAction(action, pageable);
        } else {
            page = auditLogRepository.findAll(pageable);
        }
        return PageResponse.from(page.map(auditLogMapper::toResponse));
    }

    private String truncate(String detail) {
        if (detail == null || detail.length() <= MAX_DETAIL_LENGTH) {
            return detail;
        }
        return detail.substring(0, MAX_DETAIL_LENGTH - 3) + "...";
    }
}
