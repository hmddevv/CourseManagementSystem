package com.university.coursemanagement.dto.request;

import jakarta.validation.constraints.NotNull;

public record EnrollmentRequest(

        @NotNull(message = "studentId không được để trống")
        Long studentId,

        @NotNull(message = "courseId không được để trống")
        Long courseId
) {
}
