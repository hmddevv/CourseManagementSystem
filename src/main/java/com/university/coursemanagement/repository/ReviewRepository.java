package com.university.coursemanagement.repository;

import com.university.coursemanagement.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    Optional<Review> findByStudentIdAndCourseId(Long studentId, Long courseId);

    boolean existsByCourseId(Long courseId);

    /** Danh sach danh gia cua mot khoa hoc; nap san hoc vien de tranh N+1 khi map DTO. */
    @EntityGraph(attributePaths = {"student"})
    Page<Review> findByCourseId(Long courseId, Pageable pageable);

    /**
     * Diem trung binh va so luot danh gia cho NHIEU khoa hoc trong MOT truy van.
     * Dung khi tra ve danh sach khoa hoc.
     */
    @Query("""
            SELECT r.course.id AS courseId, AVG(r.rating) AS average, COUNT(r) AS total
            FROM Review r
            WHERE r.course.id IN :courseIds
            GROUP BY r.course.id
            """)
    List<CourseRatingAggregate> aggregateByCourseIds(@Param("courseIds") Collection<Long> courseIds);

    /**
     * Xep hang khoa hoc theo diem trung binh giam dan.
     *
     * <p>Dung INNER JOIN nen khoa hoc chua ai danh gia khong xuat hien - dung y nghia
     * "bang xep hang". Phan trang thu cong tren ket qua gop vi JPQL GROUP BY khong
     * ket hop truc tiep voi Pageable dem tu dong.</p>
     */
    @Query("""
            SELECT r.course.id AS courseId, AVG(r.rating) AS average, COUNT(r) AS total
            FROM Review r
            GROUP BY r.course.id
            ORDER BY AVG(r.rating) DESC, COUNT(r) DESC
            """)
    List<CourseRatingAggregate> rankCoursesByRating();

    /** Projection cho ket qua gop diem danh gia theo khoa hoc. */
    interface CourseRatingAggregate {
        Long getCourseId();
        Double getAverage();
        Long getTotal();
    }
}
