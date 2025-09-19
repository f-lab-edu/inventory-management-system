package inventory.supplier.service.query;

public record SupplierSearchCondition(
        String nameContains,
        String brnContains,
        Boolean active
) {}


