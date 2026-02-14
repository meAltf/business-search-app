package com.alataf.business.search.dto;


public record Pagination(int page,
                         int size,
                         long totalElements,
                         int totalPages) {
}