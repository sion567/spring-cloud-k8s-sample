package com.example.dto;

import org.springframework.data.domain.Page;
import java.util.List;

public record PageResult<T>(
        List<T> content,    // 数据列表
        int page,           // 当前页码
        int size,           // 每页条数
        long totalElements, // 总条数
        int totalPages      // 总页数
) {
    public static <T> PageResult<T> of(Page<T> page) {
        return new PageResult<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
