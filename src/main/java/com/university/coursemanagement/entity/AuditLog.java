package com.university.coursemanagement.entity;

import com.university.coursemanagement.entity.enums.AuditAction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Nhat ky thao tac. Moi lan mot phuong thuc ghi cua tang Service chay thanh cong,
 * {@code AuditAspect} ghi mot dong vao day.
 *
 * <p>Bang nay chi ghi va doc, khong bao gio sua - nen khong co quan he voi entity
 * nghiep vu (chi luu {@code entityName} + {@code entityId} dang phang). Nho vay
 * xoa mot khoa hoc khong lam mat nhat ky ve khoa hoc do.</p>
 */
@Entity
@Table(
        name = "audit_logs",
        indexes = {
                @Index(name = "idx_audit_entity", columnList = "entity_name, entity_id"),
                @Index(name = "idx_audit_action", columnList = "action")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog extends BaseEntity {

    /** Ten thuc the bi tac dong, vi du "Course", "Enrollment". */
    @Column(name = "entity_name", nullable = false, length = 60)
    private String entityName;

    /** Khoa chinh cua ban ghi bi tac dong (co the null neu thao tac that bai truoc khi co id). */
    @Column(name = "entity_id")
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuditAction action;

    /** Nguoi thuc hien - lay tu header X-User, mac dinh "system". */
    @Column(nullable = false, length = 100)
    private String actor;

    /** Mo ta ngan: ten phuong thuc va tham so dau vao rut gon. */
    @Column(length = 500)
    private String detail;
}
