package com.university.coursemanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Du lieu dau vao tao/cap nhat danh muc.
 * Dung Java record cho DTO: bat bien, gon, tach biet hoan toan voi Entity.
 */
public record CategoryRequest(

        @NotBlank(message = "Ten danh muc khong duoc de trong")
        @Size(max = 100, message = "Ten danh muc toi da 100 ky tu")
        String name,

        @Size(max = 500, message = "Mo ta toi da 500 ky tu")
        String description
) {
}
