package com.university.coursemanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI courseManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hệ thống quản lý khóa học - Course Management API")
                        .description("REST API quản lý danh mục, giảng viên, học viên, khóa học, bài học và ghi danh. "
                                + "Đồ án cuối kỳ - Lập trình ứng dụng với Java.")
                        .version("1.0.0")
                        .contact(new Contact().name("Nhóm đồ án").email("chutichho75@gmail.com"))
                        .license(new License().name("Educational use")));
    }
}
