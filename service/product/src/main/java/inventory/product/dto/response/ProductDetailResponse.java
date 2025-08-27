package inventory.product.dto.response;

import java.util.List;

public record ProductDetailResponse(
        Long id,
        String name,
        String productCode,
        String description,
        Long supplierId,
        String supplierName,
        Long price,
        String category,
        List<WarehouseStockResponse> warehouseStocks
) {
    public static ProductDetailResponse of(
            final Long id,
            final String name,
            final String productCode,
            final String description,
            final Long supplierId,
            final String supplierName,
            final Long price,
            final String category,
            final List<WarehouseStockResponse> warehouseStocks
    ) {
        return new ProductDetailResponse(id, name, productCode, description, supplierId, supplierName, price, category, warehouseStocks);
    }
}
