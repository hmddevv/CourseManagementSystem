package com.university.coursemanagement.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Du lieu hoc vien gui khi danh gia mot khoa hoc. */
public record ReviewRequest(

        @NotNull(message = "studentId khong duoc de trong")
        Long studentId,

        @NotNull(message = "Diem danh gia khong duoc de trong")
        @Min(value = 1, message = "Diem danh gia phai tu 1 den 5")
        @Max(value = 5, message = "Diem danh gia phai tu 1 den 5")
        Integer rating,

        @Size(max = 1000, message = "Nhan xet toi da 1000 ky tu")
        String comment
) {
}
