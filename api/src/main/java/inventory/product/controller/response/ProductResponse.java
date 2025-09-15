package inventory.product.controller.response;

import inventory.product.domain.Product;
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

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getProductName(),
                product.getSupplierId(),
                "공급업체명", // TODO: 실제 공급업체명 조회
                product.getProductCode(),
                product.getThumbnailUrl(),
                product.getUnit(),
                product.isActive(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
