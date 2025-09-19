package inventory.warehouse.service.query;

public record WarehouseStockSearchCondition(
        Long warehouseId,
        Long productId,
        String productNameContains,
        String productCodeContains,
        Boolean belowSafetyOnly
) {}


