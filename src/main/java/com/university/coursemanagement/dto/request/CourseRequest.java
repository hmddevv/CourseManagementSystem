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

/**
 * Du lieu dau vao tao/cap nhat khoa hoc.
 * Trang thai (status) khong nam o day - duoc dieu khien qua nghiep vu
 * publish/archive rieng de dam bao rang buoc (vi du: phai co bai hoc moi publish).
 */
public record CourseRequest(

        @NotBlank(message = "Tieu de khoa hoc khong duoc de trong")
        @Size(max = 200, message = "Tieu de toi da 200 ky tu")
        String title,

        @Size(max = 2000, message = "Mo ta toi da 2000 ky tu")
        String description,

        @NotNull(message = "Trinh do khong duoc de trong")
        CourseLevel level,

        @NotNull(message = "Gia khong duoc de trong")
        @DecimalMin(value = "0.0", message = "Gia phai >= 0")
        @Digits(integer = 10, fraction = 2, message = "Gia khong hop le")
        BigDecimal price,

        @NotNull(message = "Suc chua khong duoc de trong")
        @Min(value = 1, message = "Suc chua phai >= 1")
        @Max(value = 100000, message = "Suc chua qua lon")
        Integer capacity,

        @Min(value = 0, message = "Thoi luong phai >= 0")
        Integer durationHours,

        @NotNull(message = "categoryId khong duoc de trong")
        Long categoryId,

        @NotNull(message = "instructorId khong duoc de trong")
        Long instructorId
) {
}
