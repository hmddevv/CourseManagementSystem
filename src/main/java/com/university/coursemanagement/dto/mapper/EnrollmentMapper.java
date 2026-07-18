package com.university.coursemanagement.dto.mapper;

import com.university.coursemanagement.dto.response.EnrollmentResponse;
import com.university.coursemanagement.entity.Enrollment;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentMapper {

    public EnrollmentResponse toResponse(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getStudent().getId(),
                enrollment.getStudent().getFullName(),
                enrollment.getCourse().getId(),
                enrollment.getCourse().getTitle(),
                enrollment.getStatus(),
                enrollment.getProgressPercent(),
                enrollment.getEnrolledAt(),
                enrollment.getCompletedAt()
        );
    }
}
