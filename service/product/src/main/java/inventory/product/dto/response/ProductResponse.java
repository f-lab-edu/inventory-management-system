package inventory.product.dto.response;

public record ProductResponse(
        Long id,
        String name,
        String productCode,
        String description,
        Long supplierId,
        String supplierName,
        Long price,
        String category
) {
    public static ProductResponse of(
            final Long id,
            final String name,
            final String productCode,
            final String description,
            final Long supplierId,
            final String supplierName,
            final Long price,
            final String category
    ) {
        return new ProductResponse(id, name, productCode, description, supplierId, supplierName, price, category);
    }
}
