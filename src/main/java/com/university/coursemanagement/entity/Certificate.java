package com.university.coursemanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Chung chi hoan thanh khoa hoc.
 *
 * <p>Quan he 1-1 voi {@link Enrollment}: mot ban ghi ghi danh chi duoc cap dung
 * mot chung chi. Rang buoc {@code UNIQUE(enrollment_id)} la chot chan cuoi cung
 * o CSDL, ben canh kiem tra o tang Service.</p>
 *
 * <p>Ma chung chi duoc luu lai (khong tinh lai moi lan doc) vi no la thong tin
 * da phat hanh ra ngoai - hoc vien co the da tai ve hoac chia se.</p>
 */
@Entity
@Table(
        name = "certificates",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_certificate_enrollment", columnNames = "enrollment_id"),
                @UniqueConstraint(name = "uk_certificate_code", columnNames = "code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    /** Ma tra cuu, vi du CERT-12-34-20260722-A1B2C3. */
    @Column(nullable = false, length = 60)
    private String code;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    /** Ten hoc vien va ten khoa hoc tai thoi diem cap - chup lai de chung chi khong doi khi du lieu goc doi. */
    @Column(name = "student_name", nullable = false, length = 150)
    private String studentName;

    @Column(name = "course_title", nullable = false, length = 200)
    private String courseTitle;
}
