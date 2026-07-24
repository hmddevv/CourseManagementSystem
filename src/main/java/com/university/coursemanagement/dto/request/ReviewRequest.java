package com.university.coursemanagement.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequest(

        @NotNull(message = "studentId không được để trống")
        Long studentId,

        @NotNull(message = "Điểm đánh giá không được để trống")
        @Min(value = 1, message = "Điểm đánh giá phải từ 1 đến 5")
        @Max(value = 5, message = "Điểm đánh giá phải từ 1 đến 5")
        Integer rating,

        @Size(max = 1000, message = "Nhận xét tối đa 1000 ký tự")
        String comment
) {
}
