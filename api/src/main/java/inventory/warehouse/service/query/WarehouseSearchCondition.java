package inventory.warehouse.service.query;

public record WarehouseSearchCondition(
        String nameContains,
        String postcodeContains,
        Boolean active
) {}


