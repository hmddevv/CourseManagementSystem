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

    boolean existsByStudentIdAndCourseIdAndStatus(Long studentId, Long courseId, EnrollmentStatus status);

    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    /** So cho da bi chiem (ghi danh dang ACTIVE) cua mot khoa hoc. */
    long countByCourseIdAndStatus(Long courseId, EnrollmentStatus status);

    Page<Enrollment> findByStudentId(Long studentId, Pageable pageable);

    Page<Enrollment> findByCourseId(Long courseId, Pageable pageable);

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
