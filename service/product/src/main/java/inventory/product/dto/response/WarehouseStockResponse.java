package inventory.product.dto.response;

public record WarehouseStockResponse(
        Long warehouseId,
        String warehouseName,
        Long quantity,
        Long safetyStock,
        String warehouseAddress
) {
    public static WarehouseStockResponse of(
            final Long warehouseId,
            final String warehouseName,
            final Long quantity,
            final Long safetyStock,
            final String warehouseAddress
    ) {
        return new WarehouseStockResponse(warehouseId, warehouseName, quantity, safetyStock, warehouseAddress);
    }
}
