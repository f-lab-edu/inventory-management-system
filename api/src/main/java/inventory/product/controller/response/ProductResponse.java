package inventory.product.controller.response;

import java.time.LocalDateTime;

public record ProductResponse(
        Long productId,
        String productName,
        Long supplierId,
        String supplierName,
        String productCode,
        String thumbnailUrl,
        String unit,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static ProductResponse of(
            final Long productId,
            final String productName,
            final Long supplierId,
            final String supplierName,
            final String productCode,
            final String thumbnailUrl,
            final String unit,
            final boolean active,
            final LocalDateTime createdAt,
            final LocalDateTime modifiedAt
    ) {
        return new ProductResponse(
                productId, productName, supplierId,
                supplierName, productCode, thumbnailUrl,
                unit, active, createdAt, modifiedAt
        );
    }
}
