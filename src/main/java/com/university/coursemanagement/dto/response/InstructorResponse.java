package com.university.coursemanagement.dto.response;

public record InstructorResponse(
        Long id,
        String fullName,
        String email,
        String expertise,
        String bio,
        int courseCount
) {
}
