package com.university.coursemanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Danh gia cua hoc vien cho mot khoa hoc: diem 1-5 sao kem nhan xet.
 *
 * <p>Rang buoc duy nhat (student_id, course_id): moi hoc vien chi danh gia
 * mot lan cho moi khoa hoc. Diem trung binh KHONG duoc luu tren bang courses
 * ma tinh bang truy van gop - tranh du lieu trung lap co the lech nhau.</p>
 */
@Entity
@Table(
        name = "reviews",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_review_student_course",
                columnNames = {"student_id", "course_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /** Diem danh gia 1..5, duoc chan them mot lan nua o tang DTO bang @Min/@Max. */
    @Column(nullable = false)
    private Integer rating;

    @Column(length = 1000)
    private String comment;
}
