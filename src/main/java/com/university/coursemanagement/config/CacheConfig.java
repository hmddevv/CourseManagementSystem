package com.university.coursemanagement.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bat Spring Cache. Spring tao proxy quanh cac bean co {@code @Cacheable}:
 * lan goi dau tien chay that va luu ket qua, cac lan sau tra thang tu bo nho
 * ma khong cham toi CSDL cho den khi cache bi xoa boi {@code @CacheEvict}.
 *
 * <p>Chi cache du lieu <b>doc nhieu, ghi it</b>:
 * <ul>
 *   <li>{@code categories} - danh muc gan nhu khong doi nhung duoc goi o moi man hinh</li>
 *   <li>{@code courseStatistics} - thong ke dashboard, tinh bang nhieu truy van gop</li>
 * </ul>
 *
 * <p>Dung {@link ConcurrentMapCacheManager} (cache trong bo nho tien trinh) vi du an
 * chay mot instance. Khi trien khai nhieu instance, moi instance se co ban cache
 * rieng va co the lech nhau - luc do phai chuyen sang cache phan tan (Redis).
 * Doi lai chi phai doi bean CacheManager, khong phai sua tang Service.</p>
 */
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
