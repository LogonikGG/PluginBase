package ru.logonik.pluginBase.util.page;

import java.util.Collections;
import java.util.List;

public class Page<T> {
    private final List<T> content;
    private final int pageNumber;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;

    public Page(List<T> content, int pageNumber, int pageSize, long totalElements, int totalPages) {
        this.content = Collections.unmodifiableList(content);
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<T> getContent() {
        return content;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean hasNext() {
        return pageNumber < totalPages;
    }

    public boolean hasPrevious() {
        return pageNumber > 1;
    }
}

