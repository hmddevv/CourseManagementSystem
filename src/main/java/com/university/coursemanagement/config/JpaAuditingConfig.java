package com.university.coursemanagement.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * Bat JPA Auditing: Spring Data tu dien {@code createdBy} / {@code updatedBy}
 * tren {@link com.university.coursemanagement.entity.BaseEntity} moi khi luu entity.
 *
 * <p>He thong chua co xac thuc nguoi dung, nen nguoi thao tac duoc lay tu header
 * {@code X-User} neu client gui kem, nguoc lai mac dinh la {@code system}. Khi bo
 * sung Spring Security sau nay, chi can doi than {@link AuditorAware} sang doc
 * {@code SecurityContextHolder} - khong phai sua bat ky entity nao.</p>
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    public static final String USER_HEADER = "X-User";
    public static final String SYSTEM_ACTOR = "system";

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of(currentActor());
    }

    /** Ten nguoi thao tac cua request hien tai, hoac "system" khi chay ngoai request (job, test). */
    public static String currentActor() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            HttpServletRequest request = attributes.getRequest();
            String user = request.getHeader(USER_HEADER);
            if (user != null && !user.isBlank()) {
                return user.trim();
            }
        }
        return SYSTEM_ACTOR;
    }
}
