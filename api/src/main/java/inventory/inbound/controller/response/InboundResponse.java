package inventory.inbound.controller.response;

import inventory.inbound.domain.Inbound;
import inventory.inbound.domain.InboundProduct;
import inventory.inbound.enums.InboundStatus;

import java.time.LocalDate;
import java.util.List;

public record InboundResponse(
        Long id,
        Long warehouseId,
        Long supplierId,
        LocalDate expectedDate,
        List<InboundProductResponse> products,
        InboundStatus status
) {
    public static InboundResponse from(Inbound inbound) {
        List<InboundProductResponse> productResponses = inbound.getProducts().stream()
                .map(InboundProductResponse::from)
                .toList();
        
        return new InboundResponse(
                inbound.getInboundId(),
                inbound.getWarehouseId(),
                inbound.getSupplierId(),
                inbound.getExpectedDate(),
                productResponses,
                inbound.getStatus()
        );
    }

    public record InboundProductResponse(
            Long productId,
            Integer quantity
    ) {
        public static InboundProductResponse from(InboundProduct inboundProduct) {
            return new InboundProductResponse(
                    inboundProduct.productId(),
                    inboundProduct.quantity()
            );
        }
    }
}