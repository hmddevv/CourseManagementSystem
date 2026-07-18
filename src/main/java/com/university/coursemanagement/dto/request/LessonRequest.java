package com.university.coursemanagement.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LessonRequest(

        @NotBlank(message = "Tieu de bai hoc khong duoc de trong")
        @Size(max = 200, message = "Tieu de toi da 200 ky tu")
        String title,

        @Size(max = 4000, message = "Noi dung toi da 4000 ky tu")
        String content,

        @NotNull(message = "Thu tu bai hoc khong duoc de trong")
        @Min(value = 1, message = "Thu tu bai hoc phai >= 1")
        Integer orderIndex,

        @Min(value = 0, message = "Thoi luong phai >= 0")
        Integer durationMinutes
) {
}
