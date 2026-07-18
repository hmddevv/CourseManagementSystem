package com.university.coursemanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Cau hinh tai lieu OpenAPI/Swagger. Truy cap tai /swagger-ui.html. */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI courseManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("He thong quan ly khoa hoc - Course Management API")
                        .description("REST API quan ly danh muc, giang vien, hoc vien, khoa hoc, bai hoc va ghi danh. "
                                + "Do an cuoi ky - Lap trinh ung dung voi Java.")
                        .version("1.0.0")
                        .contact(new Contact().name("Nhom do an").email("chutichho75@gmail.com"))
                        .license(new License().name("Educational use")));
    }
}
