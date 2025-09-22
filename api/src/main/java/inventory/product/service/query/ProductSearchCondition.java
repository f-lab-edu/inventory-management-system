package inventory.product.service.query;

public record ProductSearchCondition(
        Long supplierId,
        String productNameContains,
        String productCodeContains,
        Boolean active
) {}


