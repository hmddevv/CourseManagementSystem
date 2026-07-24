package com.university.coursemanagement.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LessonRequest(

        @NotBlank(message = "Tiêu đề bài học không được để trống")
        @Size(max = 200, message = "Tiêu đề tối đa 200 ký tự")
        String title,

        @Size(max = 4000, message = "Nội dung tối đa 4000 ký tự")
        String content,

        @NotNull(message = "Thứ tự bài học không được để trống")
        @Min(value = 1, message = "Thứ tự bài học phải >= 1")
        Integer orderIndex,

        @Min(value = 0, message = "Thời lượng phải >= 0")
        Integer durationMinutes
) {
}
