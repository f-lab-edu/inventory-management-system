package inventory.warehouse.service.response;

import inventory.warehouse.domain.Warehouse;

import java.time.LocalDateTime;

public record WarehouseResponse(
        Long id,
        String name,
        String postcode,
        String baseAddress,
        String detailAddress,
        String managerName,
        String managerContact,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static WarehouseResponse of(
            final Long id,
            final String name,
            final String postcode,
            final String baseAddress,
            final String detailAddress,
            final String managerName,
            final String managerContact,
            final boolean active,
            final LocalDateTime createdAt,
            final LocalDateTime modifiedAt
    ) {
        return new WarehouseResponse(id, name, postcode, baseAddress, detailAddress, managerName, managerContact, active, createdAt, modifiedAt);
    }

    public static WarehouseResponse from(Warehouse warehouse) {
        return new WarehouseResponse(
                warehouse.getWarehouseId(),
                warehouse.getName(),
                warehouse.getPostcode(),
                warehouse.getBaseAddress(),
                warehouse.getDetailAddress(),
                warehouse.getManagerName(),
                warehouse.getManagerContact(),
                warehouse.isActive(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
