package inventory.inbound.service.response;

import inventory.inbound.domain.InboundProduct;
import inventory.product.domain.Product;

public record InboundProductResponse(
        Long productId,
        String productName,
        String productCode,
        String unit,
        Integer quantity
) {
    public static InboundProductResponse from(InboundProduct inboundProduct, Product product) {
        return new InboundProductResponse(
                inboundProduct.getProductId(),
                product.getProductName(),
                product.getProductCode(),
                product.getUnit(),
                inboundProduct.getQuantity()
        );
    }
}
