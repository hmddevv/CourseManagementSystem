package com.university.coursemanagement.repository;

import com.university.coursemanagement.entity.Course;
import com.university.coursemanagement.entity.enums.CourseLevel;
import com.university.coursemanagement.entity.enums.CourseStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

/**
 * Tap hop cac {@link Specification} de xay dung truy van loc dong cho Course.
 * Moi tieu chi la mot Specification doc lap, ghep lai bang {@code and()}.
 */
public final class CourseSpecifications {

    private CourseSpecifications() {
    }

    /** Tim theo tu khoa trong tieu de HOAC mo ta (khong phan biet hoa thuong). */
    public static Specification<Course> keywordLike(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        String pattern = "%" + keyword.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), pattern),
                cb.like(cb.lower(root.get("description")), pattern)
        );
    }

    public static Specification<Course> hasCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Course> hasInstructor(Long instructorId) {
        if (instructorId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("instructor").get("id"), instructorId);
    }

    public static Specification<Course> hasLevel(CourseLevel level) {
        if (level == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("level"), level);
    }

    public static Specification<Course> hasStatus(CourseStatus status) {
        if (status == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Course> priceGreaterOrEqual(BigDecimal min) {
        if (min == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), min);
    }

    public static Specification<Course> priceLessOrEqual(BigDecimal max) {
        if (max == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), max);
    }
}
