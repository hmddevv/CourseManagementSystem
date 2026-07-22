package com.university.coursemanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Lop cha chung cho moi entity: khoa chinh tu tang, audit timestamps, nguoi thao tac
 * va optimistic-locking version. Dung {@code @MappedSuperclass} nen cac cot nay
 * duoc "nhung" vao bang con chu khong tao bang rieng.
 *
 * <p>{@code @EntityListeners(AuditingEntityListener.class)} cho phep Spring Data JPA
 * tu dong dien {@code createdBy} / {@code updatedBy} tu {@code AuditorAware}
 * (xem {@code config.JpaAuditingConfig}).</p>
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** Nguoi tao ban ghi - do AuditorAware cung cap, khong bao gio sua lai. */
    @CreatedBy
    @Column(name = "created_by", length = 100, updatable = false)
    private String createdBy;

    /** Nguoi sua ban ghi lan cuoi. */
    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    /** Optimistic locking - chong ghi de dong thoi (lost update). */
    @Version
    @Column(name = "version")
    private Long version;
}
