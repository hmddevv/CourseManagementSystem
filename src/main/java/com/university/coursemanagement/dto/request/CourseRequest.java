package com.university.coursemanagement.dto.request;

import com.university.coursemanagement.entity.enums.CourseLevel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CourseRequest(

        @NotBlank(message = "Tiêu đề khóa học không được để trống")
        @Size(max = 200, message = "Tiêu đề tối đa 200 ký tự")
        String title,

        @Size(max = 2000, message = "Mô tả tối đa 2000 ký tự")
        String description,

        @NotNull(message = "Trình độ không được để trống")
        CourseLevel level,

        @NotNull(message = "Giá không được để trống")
        @DecimalMin(value = "0.0", message = "Giá phải >= 0")
        @Digits(integer = 10, fraction = 2, message = "Giá không hợp lệ")
        BigDecimal price,

        @NotNull(message = "Sức chứa không được để trống")
        @Min(value = 1, message = "Sức chứa phải >= 1")
        @Max(value = 100000, message = "Sức chứa quá lớn")
        Integer capacity,

        @Min(value = 0, message = "Thời lượng phải >= 0")
        Integer durationHours,

        @NotNull(message = "categoryId không được để trống")
        Long categoryId,

        @NotNull(message = "instructorId không được để trống")
        Long instructorId
) {
}
