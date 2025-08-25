package inventory.common.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class PageResponse<T> {

    private List<T> content;
    private int currentPageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;

    public static <T> PageResponse<T> of(final List<T> content, final int currentPageNumber, final int pageSize, final long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        boolean hasNext = currentPageNumber < totalPages - 1;
        boolean hasPrevious = currentPageNumber > 0;

        return new PageResponse<>(content, currentPageNumber, pageSize, totalElements, totalPages, hasNext, hasPrevious);
    }
}
