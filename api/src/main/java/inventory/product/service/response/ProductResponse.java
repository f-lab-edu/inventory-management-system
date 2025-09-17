package inventory.product.service.response;

import inventory.product.domain.Product;
import inventory.supplier.domain.Supplier;

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

    public static ProductResponse from(Product product, Supplier supplier) {
        return new ProductResponse(
                product.getProductId(),
                product.getProductName(),
                supplier.getSupplierId(),
                supplier.getName(),
                product.getProductCode(),
                product.getThumbnailUrl(),
                product.getUnit(),
                product.isActive(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getProductName(),
                product.getSupplierId(),
                "공급업체", //todo: QueryDSL 페이징 도입시 없애기
                product.getProductCode(),
                product.getThumbnailUrl(),
                product.getUnit(),
                product.isActive(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
