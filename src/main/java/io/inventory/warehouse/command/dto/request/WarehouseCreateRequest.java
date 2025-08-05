package io.inventory.warehouse.command.dto.request;

public record WarehouseCreateRequest(
        String name,
        Integer postcode,
        String baseAddress,
        String detailAddress,
        String contact
) {
}
