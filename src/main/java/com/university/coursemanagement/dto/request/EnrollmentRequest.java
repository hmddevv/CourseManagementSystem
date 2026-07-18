package com.university.coursemanagement.dto.request;

import jakarta.validation.constraints.NotNull;

/** Yeu cau ghi danh mot hoc vien vao mot khoa hoc. */
public record EnrollmentRequest(

        @NotNull(message = "studentId khong duoc de trong")
        Long studentId,

        @NotNull(message = "courseId khong duoc de trong")
        Long courseId
) {
}
