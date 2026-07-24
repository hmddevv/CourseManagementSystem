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

    @EntityGraph(attributePaths = {"student"})
    Page<Review> findByCourseId(Long courseId, Pageable pageable);

    @Query("""
            SELECT r.course.id AS courseId, AVG(r.rating) AS average, COUNT(r) AS total
            FROM Review r
            WHERE r.course.id IN :courseIds
            GROUP BY r.course.id
            """)
    List<CourseRatingAggregate> aggregateByCourseIds(@Param("courseIds") Collection<Long> courseIds);

    @Query("""
            SELECT r.course.id AS courseId, AVG(r.rating) AS average, COUNT(r) AS total
            FROM Review r
            GROUP BY r.course.id
            ORDER BY AVG(r.rating) DESC, COUNT(r) DESC
            """)
    List<CourseRatingAggregate> rankCoursesByRating();

    interface CourseRatingAggregate {
        Long getCourseId();
        Double getAverage();
        Long getTotal();
    }
}
