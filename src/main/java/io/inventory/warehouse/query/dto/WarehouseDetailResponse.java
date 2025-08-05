package io.inventory.warehouse.query.dto;

import io.inventory.warehouse.domain.Warehouse;

import java.time.LocalDateTime;

public record WarehouseDetailResponse(
        Long warehouseId,
        String name,
        Integer postcode,
        String baseAddress,
        String detailAddress,
        String contact,
        LocalDateTime createdAt
) {

    public static WarehouseDetailResponse from(final Warehouse warehouse) {
        return new WarehouseDetailResponse(
                warehouse.getWarehouseId(),
                warehouse.getName(),
                warehouse.getPostcode(),
                warehouse.getBaseAddress(),
                warehouse.getDetailAddress(),
                warehouse.getContact(),
                warehouse.getCreatedAt()
        );
    }
}
