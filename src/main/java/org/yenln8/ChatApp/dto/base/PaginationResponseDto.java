package org.yenln8.ChatApp.dto.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class PaginationResponseDto<T,U> {
    private PaginationInfo pagination;
    private List<U> records;

    public static <T,U> PaginationResponseDto<T,U> of(Page<T> page, List<U> record) {
        PaginationInfo pagination = PaginationInfo.builder()
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalItems(page.getTotalElements())
                .pageSize(page.getSize())
                .build();

        return PaginationResponseDto.<T,U>builder()
                .pagination(pagination)
                .records(record)
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo {
        private int currentPage;
        private int totalPages;
        private long totalItems;
        private int pageSize;
    }
}