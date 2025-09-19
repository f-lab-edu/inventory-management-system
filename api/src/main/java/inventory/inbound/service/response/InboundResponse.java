package inventory.inbound.service.response;

import inventory.inbound.domain.Inbound;
import inventory.inbound.domain.enums.InboundStatus;
import inventory.supplier.domain.Supplier;
import inventory.warehouse.domain.Warehouse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record InboundResponse(
        Long inboundId,
        Long warehouseId,
        String warehouseName,
        Long supplierId,
        String supplierName,
        LocalDate expectedDate,
        List<InboundProductResponse> products,
        InboundStatus status,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static InboundResponse from(Inbound inbound, Warehouse warehouse, Supplier supplier, List<InboundProductResponse> products) {
        return new InboundResponse(
                inbound.getInboundId(),
                inbound.getWarehouseId(),
                warehouse.getName(),
                inbound.getSupplierId(),
                supplier.getName(),
                inbound.getExpectedDate(),
                products,
                inbound.getStatus(),
                inbound.getCreatedAt(),
                inbound.getModifiedAt()
        );
    }
}