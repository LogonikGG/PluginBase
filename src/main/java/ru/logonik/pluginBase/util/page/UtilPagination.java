package ru.logonik.pluginBase.util.page;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * Utility class for pagination of different data sources.
 */
public class UtilPagination {

    /**
     * Paginate a List.
     */
    public static <T> Page<T> paginate(List<T> items, int page, int size) {
        Objects.requireNonNull(items, "Items must not be null");
        int pg = Math.max(1, page);
        int sz = size < 1 ? items.size() : size;
        int total = items.size();
        int totalPages = (int) Math.ceil((double) total / sz);
        int from = (pg - 1) * sz;
        if (from >= total) {
            return new Page<>(Collections.emptyList(), pg, sz, total, totalPages);
        }
        int to = Math.min(from + sz, total);
        List<T> sub = new ArrayList<>(items.subList(from, to));
        return new Page<>(sub, pg, sz, total, totalPages);
    }

    /**
     * Paginate the last page of a list.
     */
    public static <T> Page<T> lastPage(List<T> items, int size) {
        Objects.requireNonNull(items, "Items must not be null");
        int sz = size < 1 ? items.size() : size;
        int total = items.size();
        int totalPages = (int) Math.ceil((double) total / sz);
        return paginate(items, totalPages, sz);
    }

    /**
     * Paginate an array.
     */
    public static <T> Page<T> paginate(T[] array, int page, int size) {
        Objects.requireNonNull(array, "Array must not be null");
        List<T> list = List.of(array);
        return paginate(list, page, size);
    }

    /**
     * Paginate any Iterable (will exhaust iterator).
     */
    public static <T> Page<T> paginate(Iterable<T> iterable, int page, int size) {
        Objects.requireNonNull(iterable, "Iterable must not be null");
        List<T> list = StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(iterable.iterator(), Spliterator.ORDERED),
                        false
                )
                .collect(Collectors.toList());
        return paginate(list, page, size);
    }

    /**
     * Paginate a Stream (will exhaust stream).
     */
    public static <T> Page<T> paginate(Stream<T> stream, int page, int size) {
        Objects.requireNonNull(stream, "Stream must not be null");
        int pg = Math.max(1, page);
        int sz = Math.max(1, size);
        // collect only window
        List<T> content = stream
                .skip((long) (pg - 1) * sz)
                .limit(sz)
                .collect(Collectors.toList());
        // totalElements unknown - use content size
        long total = content.size();
        int totalPages = content.isEmpty() ? 0 : 1;
        return new Page<>(content, pg, sz, total, totalPages);
    }

    /**
     * Helper: calculate starting index (0-based) for page in sequence.
     */
    public static int startIndex(int page, int size) {
        int pg = Math.max(1, page);
        int sz = Math.max(1, size);
        return (pg - 1) * sz;
    }

    /**
     * Helper: calculate ending index (exclusive, 0-based) for page in sequence.
     */
    public static int endIndex(int page, int size, int totalElements) {
        int start = startIndex(page, size);
        return Math.min(start + size, totalElements);
    }

    /**
     * Map contents of a page to another type.
     */
    public static <T, R> Page<R> map(Page<T> page, Function<? super T, ? extends R> mapper) {
        Objects.requireNonNull(page, "Page must not be null");
        Objects.requireNonNull(mapper, "Mapper must not be null");
        List<R> mapped = page.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());
        return new Page<>(mapped,
                page.getPageNumber(), page.getPageSize(),
                page.getTotalElements(), page.getTotalPages());
    }
}
