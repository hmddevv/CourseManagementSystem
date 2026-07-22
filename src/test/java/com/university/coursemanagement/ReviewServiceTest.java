package com.university.coursemanagement;

import com.university.coursemanagement.dto.request.EnrollmentRequest;
import com.university.coursemanagement.dto.request.ReviewRequest;
import com.university.coursemanagement.entity.Category;
import com.university.coursemanagement.entity.Course;
import com.university.coursemanagement.entity.Instructor;
import com.university.coursemanagement.entity.Lesson;
import com.university.coursemanagement.entity.Student;
import com.university.coursemanagement.entity.enums.CourseLevel;
import com.university.coursemanagement.entity.enums.CourseStatus;
import com.university.coursemanagement.exception.BusinessException;
import com.university.coursemanagement.exception.DuplicateResourceException;
import com.university.coursemanagement.repository.CategoryRepository;
import com.university.coursemanagement.repository.CourseRepository;
import com.university.coursemanagement.repository.InstructorRepository;
import com.university.coursemanagement.repository.StudentRepository;
import com.university.coursemanagement.service.CourseService;
import com.university.coursemanagement.service.EnrollmentService;
import com.university.coursemanagement.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Kiem tra quy tac nghiep vu cua danh gia khoa hoc va bang xep hang.
 */
@SpringBootTest
@ActiveProfiles("dev")
class ReviewServiceTest {

    @Autowired ReviewService reviewService;
    @Autowired EnrollmentService enrollmentService;
    @Autowired CourseService courseService;
    @Autowired CategoryRepository categoryRepository;
    @Autowired InstructorRepository instructorRepository;
    @Autowired StudentRepository studentRepository;
    @Autowired CourseRepository courseRepository;

    @Test
    void createReview_shouldFail_whenStudentNotEnrolled() {
        Course course = givenPublishedCourse();
        Student outsider = givenStudent("khach");

        assertThatThrownBy(() -> reviewService.createReview(
                course.getId(), new ReviewRequest(outsider.getId(), 5, "Chua hoc ma van danh gia")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("da ghi danh");
    }

    @Test
    void createReview_shouldFail_whenStudentReviewsTwice() {
        Course course = givenPublishedCourse();
        Student student = givenEnrolledStudent(course);

        reviewService.createReview(course.getId(), new ReviewRequest(student.getId(), 5, "Rat hay"));

        assertThatThrownBy(() -> reviewService.createReview(
                course.getId(), new ReviewRequest(student.getId(), 3, "Danh gia lai")))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void createReview_shouldSucceed_andAppearInCourseResponse() {
        Course course = givenPublishedCourse();
        Student first = givenEnrolledStudent(course);
        Student second = givenEnrolledStudent(course);

        reviewService.createReview(course.getId(), new ReviewRequest(first.getId(), 5, "Tuyet voi"));
        reviewService.createReview(course.getId(), new ReviewRequest(second.getId(), 4, "Tot"));

        var response = courseService.getById(course.getId());
        assertThat(response.reviewCount()).isEqualTo(2);
        assertThat(response.averageRating()).isEqualTo(4.5);
    }

    @Test
    void getByCourse_shouldReturnPagedReviews() {
        Course course = givenPublishedCourse();
        Student student = givenEnrolledStudent(course);
        reviewService.createReview(course.getId(), new ReviewRequest(student.getId(), 5, "Hay"));

        var page = reviewService.getByCourse(course.getId(), PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).studentName()).isEqualTo(student.getFullName());
    }

    @Test
    void getTopRated_shouldRankByAverageRatingDescending() {
        Course good = givenPublishedCourse();
        Course average = givenPublishedCourse();
        reviewService.createReview(good.getId(), new ReviewRequest(givenEnrolledStudent(good).getId(), 5, null));
        reviewService.createReview(average.getId(), new ReviewRequest(givenEnrolledStudent(average).getId(), 2, null));

        var ranking = reviewService.getTopRated(PageRequest.of(0, 50)).getContent();

        int positionOfGood = indexOfCourse(ranking, good.getId());
        int positionOfAverage = indexOfCourse(ranking, average.getId());
        assertThat(positionOfGood).isGreaterThanOrEqualTo(0);
        assertThat(positionOfAverage).isGreaterThan(positionOfGood);
    }

    // ----- du lieu mau -----

    private int indexOfCourse(java.util.List<com.university.coursemanagement.dto.response.CourseRatingResponse> list,
                              Long courseId) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).courseId().equals(courseId)) {
                return i;
            }
        }
        return -1;
    }

    private Student givenStudent(String prefix) {
        return studentRepository.save(Student.builder()
                .fullName("Hoc vien " + prefix)
                .email("%s-%d@t.edu".formatted(prefix, System.nanoTime()))
                .build());
    }

    private Student givenEnrolledStudent(Course course) {
        Student student = givenStudent("hv");
        enrollmentService.enroll(new EnrollmentRequest(student.getId(), course.getId()));
        return student;
    }

    private Course givenPublishedCourse() {
        Category category = categoryRepository.save(
                Category.builder().name("Rv-Cat-" + System.nanoTime()).build());
        Instructor instructor = instructorRepository.save(Instructor.builder()
                .fullName("GV Review").email("gv-rv-" + System.nanoTime() + "@t.edu").build());
        Course course = Course.builder()
                .title("Khoa hoc co danh gia").level(CourseLevel.BEGINNER).status(CourseStatus.DRAFT)
                .price(BigDecimal.ZERO).capacity(50).category(category).instructor(instructor).build();
        course.addLesson(Lesson.builder().title("Bai 1").orderIndex(1).build());
        course.setStatus(CourseStatus.PUBLISHED);
        return courseRepository.save(course);
    }
}
