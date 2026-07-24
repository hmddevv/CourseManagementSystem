package com.university.coursemanagement.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    public static final String CATEGORIES_CACHE = "categories";
    public static final String COURSE_STATISTICS_CACHE = "courseStatistics";

    @Bean
    public ConcurrentMapCacheManager cacheManager() {
        return new ConcurrentMapCacheManager(CATEGORIES_CACHE, COURSE_STATISTICS_CACHE);
    }
}
