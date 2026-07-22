package com.university.coursemanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Bat co che job dinh ky ({@code @Scheduled}).
 *
 * <p>Mac dinh Spring dung mot luong duy nhat cho moi job da lich, nen job chay lau
 * se lam tre cac job khac. Du an hien chi co mot job nen giu mac dinh cho don gian;
 * khi them job se can khai bao {@code TaskScheduler} co pool nhieu luong.</p>
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
