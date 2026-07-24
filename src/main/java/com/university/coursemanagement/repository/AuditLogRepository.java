package com.university.coursemanagement.repository;

import com.university.coursemanagement.entity.AuditLog;
import com.university.coursemanagement.entity.enums.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findByEntityName(String entityName, Pageable pageable);

    Page<AuditLog> findByAction(AuditAction action, Pageable pageable);

    Page<AuditLog> findByEntityNameAndAction(String entityName, AuditAction action, Pageable pageable);
}
