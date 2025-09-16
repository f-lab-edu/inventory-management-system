package inventory.inbound.controller.response;

import inventory.inbound.domain.InboundProduct;
import inventory.product.domain.Product;

public record InboundProductResponse(
        Long productId,
        String productName,
        Integer quantity
) {
    public static InboundProductResponse from(InboundProduct inboundProduct, Product product) {
        return new InboundProductResponse(
                inboundProduct.getProductId(), product.getProductName(), inboundProduct.getQuantity()
        );
    }

}
