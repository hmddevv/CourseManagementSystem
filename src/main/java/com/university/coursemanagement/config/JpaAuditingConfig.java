package com.university.coursemanagement.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {
    public static final String USER_HEADER = "X-User";
    public static final String SYSTEM_ACTOR = "system";

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of(currentActor());
    }

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
