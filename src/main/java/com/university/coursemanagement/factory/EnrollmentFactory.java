package com.university.coursemanagement.factory;

import com.university.coursemanagement.entity.Course;
import com.university.coursemanagement.entity.Enrollment;
import com.university.coursemanagement.entity.Student;
import com.university.coursemanagement.entity.enums.EnrollmentStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * FACTORY PATTERN.
 *
 * <p>Tap trung logic khoi tao mot {@link Enrollment} moi vao mot noi duy nhat:
 * thoi diem ghi danh = now, trang thai mac dinh = ACTIVE, tien do = 0. Nho vay
 * Service khong phai lap lai cach dung, va neu quy tac khoi tao thay doi ta chi
 * sua o day. Ket hop voi BUILDER PATTERN (Lombok @Builder) tren entity.</p>
 */
@Component
public class EnrollmentFactory {

    public Enrollment createActiveEnrollment(Student student, Course course) {
        return Enrollment.builder()
                .student(student)
                .course(course)
                .enrolledAt(LocalDateTime.now())
                .status(EnrollmentStatus.ACTIVE)
                .progressPercent(0)
                .build();
    }
}
