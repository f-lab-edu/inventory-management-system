package inventory.outbound.service.response;

import inventory.outbound.domain.Outbound;
import inventory.outbound.domain.enums.OutboundStatus;
import inventory.warehouse.domain.Warehouse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record OutboundResponse(
        Long outboundId,
        String orderNumber,
        Long warehouseId,
        String warehouseName,
        String recipientName,
        String recipientContact,
        String deliveryPostcode,
        String deliveryBaseAddress,
        String deliveryDetailAddress,
        LocalDate requestedDate,
        LocalDate expectedDate,
        String deliveryMemo,
        OutboundStatus status,
        List<OutboundProductResponse> products,
        StockSummary stockSummary,
        LocalDateTime createdAt
) {
    public static OutboundResponse from(
            Outbound outbound,
            Warehouse warehouse,
            List<OutboundProductResponse> products
    ) {
        StockSummary summary = generateStockSummary(products);

        return new OutboundResponse(
                outbound.getOutboundId(),
                outbound.getOrderNumber(),
                outbound.getWarehouseId(),
                warehouse.getName(),
                outbound.getRecipientName(),
                outbound.getRecipientContact(),
                outbound.getDeliveryPostcode(),
                outbound.getDeliveryBaseAddress(),
                outbound.getDeliveryDetailAddress(),
                outbound.getRequestedDate(),
                outbound.getExpectedDate(),
                outbound.getDeliveryMemo(),
                outbound.getOutboundStatus(),
                products,
                summary,
                outbound.getCreatedAt()
        );
    }

    private static StockSummary generateStockSummary(List<OutboundProductResponse> products) {
        int totalProductCount = products.size();
        int lowStockProductCount = (int) products.stream()
                .filter(OutboundProductResponse::isBelowSafetyStock)
                .count();
        boolean hasInsufficientStock = products.stream()
                .anyMatch(p -> p.afterOutboundStock() < 0);

        return new StockSummary(totalProductCount, lowStockProductCount, hasInsufficientStock);
    }
}
