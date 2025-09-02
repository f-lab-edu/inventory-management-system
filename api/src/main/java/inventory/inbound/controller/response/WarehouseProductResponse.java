package inventory.inbound.controller.response;

import java.time.LocalDateTime;

public record WarehouseProductResponse(
        Long id,
        Long warehouseId,
        String warehouseName,
        Long productId,
        String productName,
        Integer currentStock,
        Integer safetyStock,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static WarehouseProductResponse of(
            final Long id,
            final Long warehouseId,
            final String warehouseName,
            final Long productId,
            final String productName,
            final Integer currentStock,
            final Integer safetyStock,
            final LocalDateTime createdAt,
            final LocalDateTime modifiedAt
    ) {
        return new WarehouseProductResponse(
                id, warehouseId, warehouseName, productId, productName,
                currentStock, safetyStock, createdAt, modifiedAt
        );
    }
}
