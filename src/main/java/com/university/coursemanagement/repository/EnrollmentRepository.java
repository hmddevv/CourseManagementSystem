package com.university.coursemanagement.repository;

import com.university.coursemanagement.entity.Enrollment;
import com.university.coursemanagement.entity.enums.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    /** Hoc vien co bat ky lich su ghi danh nao khong - dung de chan xoa hoc vien. */
    boolean existsByStudentId(Long studentId);

    /** Khoa hoc co bat ky lich su ghi danh nao khong - dung de chan xoa khoa hoc. */
    boolean existsByCourseId(Long courseId);

    /** So ghi danh cua mot hoc vien - dung count query thay vi nap collection LAZY. */
    long countByStudentId(Long studentId);

    /** So cho da bi chiem (ghi danh dang ACTIVE) cua mot khoa hoc. */
    long countByCourseIdAndStatus(Long courseId, EnrollmentStatus status);

    Page<Enrollment> findByStudentId(Long studentId, Pageable pageable);

    Page<Enrollment> findByCourseId(Long courseId, Pageable pageable);

    /**
     * Dem so ghi danh theo trang thai cho NHIEU khoa hoc trong MOT truy van.
     * Dung khi tra ve danh sach khoa hoc de tranh N truy van dem rieng le.
     */
    @Query("""
            SELECT e.course.id AS courseId, COUNT(e) AS total
            FROM Enrollment e
            WHERE e.course.id IN :courseIds AND e.status = :status
            GROUP BY e.course.id
            """)
    java.util.List<CourseEnrollmentCount> countGroupedByCourseIds(
            @Param("courseIds") java.util.Collection<Long> courseIds,
            @Param("status") EnrollmentStatus status);

    /** Thong ke: dem so ghi danh ACTIVE cho tung khoa hoc (dung cho "khoa hoc pho bien"). */
    @Query("""
            SELECT e.course.id AS courseId, COUNT(e) AS total
            FROM Enrollment e
            WHERE e.status = :status
            GROUP BY e.course.id
            ORDER BY COUNT(e) DESC
            """)
    java.util.List<CourseEnrollmentCount> countActiveGroupedByCourse(@Param("status") EnrollmentStatus status);

    /** Projection cho thong ke ghi danh theo khoa hoc. */
    interface CourseEnrollmentCount {
        Long getCourseId();
        Long getTotal();
    }
}
