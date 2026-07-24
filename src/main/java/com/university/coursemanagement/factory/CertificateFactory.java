package com.university.coursemanagement.factory;

import com.university.coursemanagement.entity.Certificate;
import com.university.coursemanagement.entity.Enrollment;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CertificateFactory {
    private static final DateTimeFormatter DATE_PART = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int RANDOM_LENGTH = 6;

    private final SecureRandom random = new SecureRandom();

    public Certificate createFor(Enrollment enrollment) {
        LocalDateTime now = LocalDateTime.now();
        return Certificate.builder()
                .enrollment(enrollment)
                .code(generateCode(enrollment, now))
                .issuedAt(now)
                .studentName(enrollment.getStudent().getFullName())
                .courseTitle(enrollment.getCourse().getTitle())
                .build();
    }

    private String generateCode(Enrollment enrollment, LocalDateTime issuedAt) {
        return "CERT-%d-%d-%s-%s".formatted(
                enrollment.getCourse().getId(),
                enrollment.getStudent().getId(),
                issuedAt.format(DATE_PART),
                randomSuffix());
    }

    private String randomSuffix() {
        StringBuilder suffix = new StringBuilder(RANDOM_LENGTH);
        for (int i = 0; i < RANDOM_LENGTH; i++) {
            suffix.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return suffix.toString();
    }
}
