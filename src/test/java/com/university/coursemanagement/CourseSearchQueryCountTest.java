package com.university.coursemanagement;

import com.university.coursemanagement.dto.request.CourseSearchCriteria;
import com.university.coursemanagement.dto.request.EnrollmentRequest;
import com.university.coursemanagement.entity.Category;
import com.university.coursemanagement.entity.Course;
import com.university.coursemanagement.entity.Instructor;
import com.university.coursemanagement.entity.Lesson;
import com.university.coursemanagement.entity.Student;
import com.university.coursemanagement.entity.enums.CourseLevel;
import com.university.coursemanagement.entity.enums.CourseStatus;
import com.university.coursemanagement.repository.CategoryRepository;
import com.university.coursemanagement.repository.CourseRepository;
import com.university.coursemanagement.repository.InstructorRepository;
import com.university.coursemanagement.repository.StudentRepository;
import com.university.coursemanagement.service.CourseService;
import com.university.coursemanagement.service.EnrollmentService;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:querycountdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
        "spring.jpa.properties.hibernate.generate_statistics=true"
})
@ActiveProfiles("test")
class CourseSearchQueryCountTest {
    private static final int SEEDED_COURSES = 12;

    @Autowired CourseService courseService;
    @Autowired EnrollmentService enrollmentService;
    @Autowired CategoryRepository categoryRepository;
    @Autowired InstructorRepository instructorRepository;
    @Autowired StudentRepository studentRepository;
    @Autowired CourseRepository courseRepository;
    @Autowired EntityManagerFactory entityManagerFactory;

    private Statistics statistics;

    @BeforeEach
    void setUp() {
        statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        if (courseRepository.count() < SEEDED_COURSES) {
            seedCourses();
        }
    }

    @Test
    void search_shouldUseConstantNumberOfQueries_regardlessOfPageSize() {
        long queriesForFiveRows = countQueriesForSearch(5);
        long queriesForTenRows = countQueriesForSearch(10);

        assertThat(queriesForTenRows).isEqualTo(queriesForFiveRows);
        assertThat(queriesForTenRows).isLessThanOrEqualTo(6);
    }

    @Test
    void search_shouldStillReturnCategoryAndInstructorNames() {
        var page = courseService.search(emptyCriteria(), PageRequest.of(0, 5));

        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent()).allSatisfy(c -> {
            assertThat(c.categoryName()).isNotBlank();
            assertThat(c.instructorName()).isNotBlank();
        });
    }

    private long countQueriesForSearch(int pageSize) {
        statistics.clear();
        var page = courseService.search(emptyCriteria(), PageRequest.of(0, pageSize));
        assertThat(page.getContent()).hasSize(pageSize);
        return statistics.getPrepareStatementCount();
    }

    private CourseSearchCriteria emptyCriteria() {
        return new CourseSearchCriteria(null, null, null, null, null, null, null);
    }

    private void seedCourses() {
        Category category = categoryRepository.save(
                Category.builder().name("QC-Cat-" + System.nanoTime()).build());
        Instructor instructor = instructorRepository.save(Instructor.builder()
                .fullName("GV QC").email("gv-qc-" + System.nanoTime() + "@t.edu").build());

        for (int i = 0; i < SEEDED_COURSES; i++) {
            Course course = Course.builder()
                    .title("Khoa hoc QC " + i)
                    .level(CourseLevel.BEGINNER)
                    .status(CourseStatus.DRAFT)
                    .price(BigDecimal.ZERO)
                    .capacity(50)
                    .category(category)
                    .instructor(instructor)
                    .build();
            course.addLesson(Lesson.builder().title("Bai 1").orderIndex(1).build());
            course.addLesson(Lesson.builder().title("Bai 2").orderIndex(2).build());
            course.setStatus(CourseStatus.PUBLISHED);
            Course saved = courseRepository.save(course);

            Student student = studentRepository.save(Student.builder()
                    .fullName("HV QC " + i)
                    .email("hv-qc-%d-%d@t.edu".formatted(i, System.nanoTime()))
                    .build());
            enrollmentService.enroll(new EnrollmentRequest(student.getId(), saved.getId()));
        }
    }
}
