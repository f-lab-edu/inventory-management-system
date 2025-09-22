package inventory.outbound.service.response;

import inventory.outbound.domain.OutboundProduct;
import inventory.product.domain.Product;
import inventory.warehouse.domain.WarehouseStock;

public record OutboundProductResponse(
        Long outboundProductId,
        Long productId,
        String productName,
        String productCode,
        String unit,
        int requestedQuantity,
        int currentStock,
        int afterOutboundStock,
        int safetyStock,
        boolean isBelowSafetyStock
) {
    public static OutboundProductResponse from(
            OutboundProduct outboundProduct,
            Product product,
            WarehouseStock warehouseStock
    ) {
        int currentStock = warehouseStock.getQuantity();
        int requestedQuantity = outboundProduct.getRequestedQuantity();
        int afterOutboundStock = currentStock - requestedQuantity;
        int safetyStock = warehouseStock.getSafetyStock();
        boolean isBelowSafetyStock = afterOutboundStock < safetyStock;

        return new OutboundProductResponse(
                outboundProduct.getOutboundProductId(),
                outboundProduct.getProductId(),
                product.getProductName(),
                product.getProductCode(),
                product.getUnit(),
                outboundProduct.getRequestedQuantity(),
                warehouseStock.getQuantity(),
                afterOutboundStock,
                safetyStock,
                isBelowSafetyStock
        );
    }
}
