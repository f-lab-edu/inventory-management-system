package inventory.inbound.controller.response;

import inventory.inbound.domain.Inbound;
import inventory.inbound.enums.InboundStatus;
import inventory.supplier.domain.Supplier;
import inventory.warehouse.domain.Warehouse;

import java.time.LocalDate;
import java.util.List;

public record InboundResponse(
        Long id,
        Long warehouseId,
        String warehouseName,
        Long supplierId,
        String supplierName,
        LocalDate expectedDate,
        List<InboundProductResponse> products,
        InboundStatus status
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
                inbound.getStatus()
        );
    }
}