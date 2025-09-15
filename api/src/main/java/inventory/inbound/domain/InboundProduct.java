package inventory.inbound.domain;

import inventory.inbound.controller.request.InboundProductRequest;
import jakarta.persistence.Embeddable;

@Embeddable
public record InboundProduct(Long productId, Integer quantity) {

    public static InboundProduct from(InboundProductRequest request) {
        return new InboundProduct(request.productId(), request.quantity());
    }
}
