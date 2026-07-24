package com.university.coursemanagement;

import com.university.coursemanagement.dto.request.EnrollmentRequest;
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
import com.university.coursemanagement.service.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("dev")
class EnrollmentServiceTest {
    @Autowired EnrollmentService enrollmentService;
    @Autowired CategoryRepository categoryRepository;
    @Autowired InstructorRepository instructorRepository;
    @Autowired StudentRepository studentRepository;
    @Autowired CourseRepository courseRepository;

    @Test
    void enroll_shouldFail_whenCourseIsFull() {
        Category cat = categoryRepository.save(Category.builder().name("Test-Cat-" + System.nanoTime()).build());
        Instructor ins = instructorRepository.save(Instructor.builder()
                .fullName("GV Test").email("gv-" + System.nanoTime() + "@t.edu").build());

        Course course = Course.builder()
                .title("Khoa day").level(CourseLevel.BEGINNER).status(CourseStatus.DRAFT)
                .price(BigDecimal.ZERO).capacity(1).category(cat).instructor(ins).build();
        course.addLesson(Lesson.builder().title("B1").orderIndex(1).build());
        course.setStatus(CourseStatus.PUBLISHED);
        course = courseRepository.save(course);

        Student s1 = studentRepository.save(Student.builder()
                .fullName("HV1").email("hv1-" + System.nanoTime() + "@t.edu").build());
        Student s2 = studentRepository.save(Student.builder()
                .fullName("HV2").email("hv2-" + System.nanoTime() + "@t.edu").build());

        var ok = enrollmentService.enroll(new EnrollmentRequest(s1.getId(), course.getId()));
        assertThat(ok.id()).isNotNull();

        Long courseId = course.getId();
        assertThatThrownBy(() -> enrollmentService.enroll(new EnrollmentRequest(s2.getId(), courseId)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("đã đầy");
    }

    @Test
    void enroll_shouldFail_whenCourseNotPublished() {
        Category cat = categoryRepository.save(Category.builder().name("Cat-" + System.nanoTime()).build());
        Instructor ins = instructorRepository.save(Instructor.builder()
                .fullName("GV").email("gv2-" + System.nanoTime() + "@t.edu").build());
        Course draft = courseRepository.save(Course.builder()
                .title("Khoa nhap").level(CourseLevel.BEGINNER).status(CourseStatus.DRAFT)
                .price(BigDecimal.ZERO).capacity(10).category(cat).instructor(ins).build());
        Student s = studentRepository.save(Student.builder()
                .fullName("HV").email("hv3-" + System.nanoTime() + "@t.edu").build());

        Long courseId = draft.getId();
        assertThatThrownBy(() -> enrollmentService.enroll(new EnrollmentRequest(s.getId(), courseId)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("chưa được xuất bản");
    }
}
