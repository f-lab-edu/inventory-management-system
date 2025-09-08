package inventory.warehouse.controller.response;

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
            final LocalDateTime createdAt,
            final LocalDateTime modifiedAt
    ) {
        return new WarehouseResponse(id, name, postcode, baseAddress, detailAddress, managerName, managerContact, createdAt, modifiedAt);
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
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
