package com.university.coursemanagement.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Cap nhat tien do hoc tap cua mot ghi danh. */
public record UpdateProgressRequest(

        @NotNull(message = "progressPercent khong duoc de trong")
        @Min(value = 0, message = "Tien do phai >= 0")
        @Max(value = 100, message = "Tien do phai <= 100")
        Integer progressPercent
) {
}
