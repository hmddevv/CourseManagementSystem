package com.university.coursemanagement.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateProgressRequest(

        @NotNull(message = "progressPercent không được để trống")
        @Min(value = 0, message = "Tiến độ phải >= 0")
        @Max(value = 100, message = "Tiến độ phải <= 100")
        Integer progressPercent
) {
}
