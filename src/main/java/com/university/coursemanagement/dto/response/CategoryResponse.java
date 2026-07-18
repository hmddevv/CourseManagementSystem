package com.university.coursemanagement.dto.response;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long id,
        String name,
        String description,
        int courseCount,
        LocalDateTime createdAt
) {
}
