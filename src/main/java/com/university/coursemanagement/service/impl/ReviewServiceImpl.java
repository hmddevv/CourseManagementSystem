package com.university.coursemanagement.service.impl;

import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.mapper.ReviewMapper;
import com.university.coursemanagement.dto.request.ReviewRequest;
import com.university.coursemanagement.dto.response.CourseRatingResponse;
import com.university.coursemanagement.dto.response.ReviewResponse;
import com.university.coursemanagement.entity.Course;
import com.university.coursemanagement.entity.Review;
import com.university.coursemanagement.entity.Student;
import com.university.coursemanagement.exception.BusinessException;
import com.university.coursemanagement.exception.DuplicateResourceException;
import com.university.coursemanagement.exception.ResourceNotFoundException;
import com.university.coursemanagement.repository.CourseRepository;
import com.university.coursemanagement.repository.EnrollmentRepository;
import com.university.coursemanagement.repository.ReviewRepository;
import com.university.coursemanagement.repository.StudentRepository;
import com.university.coursemanagement.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ReviewMapper reviewMapper;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             CourseRepository courseRepository,
                             StudentRepository studentRepository,
                             EnrollmentRepository enrollmentRepository,
                             ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.reviewMapper = reviewMapper;
    }

    /**
     * Quy tac nghiep vu: chi hoc vien DA ghi danh khoa hoc moi duoc danh gia,
     * va moi hoc vien chi danh gia mot lan cho moi khoa hoc.
     */
    @Override
    @Transactional
    public ReviewResponse createReview(Long courseId, ReviewRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> ResourceNotFoundException.of("khoa hoc", courseId));
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> ResourceNotFoundException.of("hoc vien", request.studentId()));

        if (enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId).isEmpty()) {
            throw new BusinessException("Chi hoc vien da ghi danh khoa hoc moi duoc danh gia.");
        }
        if (reviewRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new DuplicateResourceException("Hoc vien da danh gia khoa hoc nay roi.");
        }

        Review review = reviewRepository.save(Review.builder()
                .student(student)
                .course(course)
                .rating(request.rating())
                .comment(request.comment())
                .build());
        return reviewMapper.toResponse(review);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request) {
        Review review = findOrThrow(reviewId);
        if (!review.getStudent().getId().equals(request.studentId())) {
            throw new BusinessException("Chi nguoi viet danh gia moi duoc sua danh gia do.");
        }
        review.setRating(request.rating());
        review.setComment(request.comment());
        return reviewMapper.toResponse(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        reviewRepository.delete(findOrThrow(reviewId));
    }

    @Override
    public PageResponse<ReviewResponse> getByCourse(Long courseId, Pageable pageable) {
        if (!courseRepository.existsById(courseId)) {
            throw ResourceNotFoundException.of("khoa hoc", courseId);
        }
        return PageResponse.from(reviewRepository.findByCourseId(courseId, pageable).map(reviewMapper::toResponse));
    }

    /**
     * Bang xep hang: truy van gop tra ve toan bo khoa hoc DA co danh gia (thuong it
     * hon nhieu so voi tong so khoa hoc), sau do cat trang va chi nap thong tin hien
     * thi cua dung cac khoa hoc trong trang do.
     */
    @Override
    public PageResponse<CourseRatingResponse> getTopRated(Pageable pageable) {
        List<ReviewRepository.CourseRatingAggregate> ranked = reviewRepository.rankCoursesByRating();

        int from = (int) Math.min(pageable.getOffset(), ranked.size());
        int to = Math.min(from + pageable.getPageSize(), ranked.size());
        List<ReviewRepository.CourseRatingAggregate> pageItems = ranked.subList(from, to);

        Map<Long, Course> courses = courseRepository
                .findAllById(pageItems.stream().map(ReviewRepository.CourseRatingAggregate::getCourseId).toList())
                .stream()
                .collect(Collectors.toMap(Course::getId, Function.identity()));

        List<CourseRatingResponse> content = pageItems.stream()
                .map(item -> toRatingResponse(item, courses.get(item.getCourseId())))
                .filter(Objects::nonNull)
                .toList();

        Page<CourseRatingResponse> page = new PageImpl<>(content, pageable, ranked.size());
        return PageResponse.from(page);
    }

    private CourseRatingResponse toRatingResponse(ReviewRepository.CourseRatingAggregate item, Course course) {
        if (course == null) {
            return null;
        }
        return new CourseRatingResponse(
                course.getId(),
                course.getTitle(),
                course.getCategory().getName(),
                course.getInstructor().getFullName(),
                round(item.getAverage()),
                item.getTotal());
    }

    /** Lam tron 1 chu so thap phan cho de doc tren giao dien (vi du 4.7). */
    private double round(Double value) {
        return value == null ? 0d : Math.round(value * 10d) / 10d;
    }

    private Review findOrThrow(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("danh gia", id));
    }
}
