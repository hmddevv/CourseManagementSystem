package com.university.coursemanagement;

import com.university.coursemanagement.dto.request.CourseRequest;
import com.university.coursemanagement.dto.request.EnrollmentRequest;
import com.university.coursemanagement.dto.request.LessonRequest;
import com.university.coursemanagement.entity.Category;
import com.university.coursemanagement.entity.Course;
import com.university.coursemanagement.entity.Instructor;
import com.university.coursemanagement.entity.Lesson;
import com.university.coursemanagement.entity.Student;
import com.university.coursemanagement.entity.enums.CourseLevel;
import com.university.coursemanagement.entity.enums.CourseStatus;
import com.university.coursemanagement.exception.BusinessException;
import com.university.coursemanagement.repository.CategoryRepository;
import com.university.coursemanagement.repository.CourseRepository;
import com.university.coursemanagement.repository.InstructorRepository;
import com.university.coursemanagement.repository.StudentRepository;
import com.university.coursemanagement.service.CourseService;
import com.university.coursemanagement.service.EnrollmentService;
import com.university.coursemanagement.service.LessonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("dev")
class CourseServiceTest {
    @Autowired CourseService courseService;
    @Autowired EnrollmentService enrollmentService;
    @Autowired LessonService lessonService;
    @Autowired CategoryRepository categoryRepository;
    @Autowired InstructorRepository instructorRepository;
    @Autowired StudentRepository studentRepository;
    @Autowired CourseRepository courseRepository;

    @Test
    void publish_shouldFail_whenCourseHasNoLesson() {
        Course empty = courseRepository.save(newDraftCourse(false));

        assertThatThrownBy(() -> courseService.publish(empty.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ít nhất 1 bài học");
    }

    @Test
    void publish_shouldSucceed_whenCourseHasLesson() {
        Course course = courseRepository.save(newDraftCourse(true));

        assertThat(courseService.publish(course.getId()).status()).isEqualTo(CourseStatus.PUBLISHED);
    }

    @Test
    void delete_shouldFail_whenCourseHasCancelledEnrollmentOnly() {
        Course course = givenPublishedCourse();
        Student student = studentRepository.save(Student.builder()
                .fullName("Hoc vien huy").email("cancel-" + System.nanoTime() + "@t.edu").build());

        var enrollment = enrollmentService.enroll(new EnrollmentRequest(student.getId(), course.getId()));
        enrollmentService.cancel(enrollment.id());

        Long courseId = course.getId();

        assertThatThrownBy(() -> courseService.delete(courseId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("lịch sử ghi danh");

        assertThat(courseRepository.existsById(courseId)).isTrue();
    }

    @Test
    void delete_shouldSucceed_whenCourseHasNoEnrollment() {
        Course course = courseRepository.save(newDraftCourse(true));

        courseService.delete(course.getId());

        assertThat(courseRepository.existsById(course.getId())).isFalse();
    }

    @Test
    void getStatistics_shouldCountPublishedAndDraftCourses() {
        var before = courseService.getStatistics();

        Category category = categoryRepository.save(
                Category.builder().name("Stat-Cat-" + System.nanoTime()).build());
        Instructor instructor = instructorRepository.save(Instructor.builder()
                .fullName("GV Stat").email("gv-stat-" + System.nanoTime() + "@t.edu").build());

        courseService.create(courseRequest("Khoa nhap thong ke", category, instructor));

        var toPublish = courseService.create(courseRequest("Khoa xuat ban", category, instructor));
        lessonService.addLesson(toPublish.id(), new LessonRequest("Bai 1", null, 1, 30));
        courseService.publish(toPublish.id());

        var after = courseService.getStatistics();

        assertThat(after.draftCourses()).isEqualTo(before.draftCourses() + 1);
        assertThat(after.publishedCourses()).isEqualTo(before.publishedCourses() + 1);
        assertThat(after.totalCourses()).isEqualTo(before.totalCourses() + 2);
    }

    private CourseRequest courseRequest(String title, Category category, Instructor instructor) {
        return new CourseRequest(title, "mo ta", CourseLevel.BEGINNER, BigDecimal.ZERO,
                30, 10, category.getId(), instructor.getId());
    }

    private Course newDraftCourse(boolean withLesson) {
        Category category = categoryRepository.save(
                Category.builder().name("Cat-" + System.nanoTime()).build());
        Instructor instructor = instructorRepository.save(Instructor.builder()
                .fullName("GV").email("gv-c-" + System.nanoTime() + "@t.edu").build());
        Course course = Course.builder()
                .title("Khoa hoc nhap").level(CourseLevel.BEGINNER).status(CourseStatus.DRAFT)
                .price(BigDecimal.ZERO).capacity(30).category(category).instructor(instructor).build();
        if (withLesson) {
            course.addLesson(Lesson.builder().title("Bai 1").orderIndex(1).build());
        }
        return course;
    }

    private Course givenPublishedCourse() {
        Course course = newDraftCourse(true);
        course.setStatus(CourseStatus.PUBLISHED);
        return courseRepository.save(course);
    }
}
