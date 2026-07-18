package com.university.coursemanagement.dto.response;

public record StudentResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        int enrollmentCount
) {
}
