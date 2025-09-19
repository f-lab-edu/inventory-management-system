package inventory.warehouse.service.response;

import inventory.product.domain.Product;
import inventory.warehouse.domain.Warehouse;
import inventory.warehouse.domain.WarehouseStock;

import java.time.LocalDateTime;

public record WarehouseStockResponse(
        Long warehouseStockId,
        Long warehouseId,
        String warehouseName,
        Long productId,
        String productName,
        String productCode,
        int quantity,
        int safetyStock,
        boolean isBelowSafetyStock,
        LocalDateTime modifiedAt
) {
    public static WarehouseStockResponse from(WarehouseStock warehouseStock, Warehouse warehouse, Product product) {
        return new WarehouseStockResponse(
                warehouseStock.getWarehouseStockId(),
                warehouseStock.getWarehouseId(),
                warehouse.getName(),
                warehouseStock.getProductId(),
                product.getProductName(),
                product.getProductCode(),
                warehouseStock.getQuantity(),
                warehouseStock.getSafetyStock(),
                warehouseStock.isBelowSafetyStock(),
                warehouseStock.getModifiedAt()
        );
    }
}
