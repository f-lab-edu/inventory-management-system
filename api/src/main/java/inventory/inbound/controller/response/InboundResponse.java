package inventory.inbound.controller.response;

import inventory.inbound.enums.InboundStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record InboundResponse(
        Long id,
        Long warehouseId,
        String warehouseName,
        Long productId,
        String productName,
        Long supplierId,
        String supplierName,
        LocalDate expectedDate,
        Integer quantity,
        InboundStatus status,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static InboundResponse of(
            final Long id,
            final Long warehouseId,
            final String warehouseName,
            final Long productId,
            final String productName,
            final Long supplierId,
            final String supplierName,
            final LocalDate expectedDate,
            final Integer quantity,
            final InboundStatus status,
            final LocalDateTime createdAt,
            final LocalDateTime modifiedAt
    ) {
        return new InboundResponse(
                id, warehouseId, warehouseName, productId, productName,
                supplierId, supplierName, expectedDate, quantity, status,
                createdAt, modifiedAt
        );
    }
}
