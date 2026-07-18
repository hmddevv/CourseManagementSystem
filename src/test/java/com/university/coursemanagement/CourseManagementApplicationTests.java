package com.university.coursemanagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/** Kiem tra Spring context khoi tao thanh cong (smoke test). */
@SpringBootTest
@ActiveProfiles("dev")
class CourseManagementApplicationTests {

    @Test
    void contextLoads() {
        // Neu context loi (thieu bean, sai cau hinh) test se fail.
    }
}
