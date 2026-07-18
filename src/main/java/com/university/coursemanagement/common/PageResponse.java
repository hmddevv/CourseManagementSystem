package com.university.coursemanagement.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Bao ket qua phan trang gon gang (an bot metadata thua cua Spring Page).
 * Dung cho moi endpoint co Paging + Sorting.
 *
 * @param <T> kieu phan tu trong trang
 */
@Getter
@Builder
@AllArgsConstructor
public class PageResponse<T> {

    private final List<T> content;
    private final int page;          // trang hien tai (0-based)
    private final int size;          // kich thuoc trang
    private final long totalElements;
    private final int totalPages;
    private final boolean first;
    private final boolean last;

    /** Chuyen doi Spring {@link Page} sang PageResponse. */
    public static <T> PageResponse<T> from(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
