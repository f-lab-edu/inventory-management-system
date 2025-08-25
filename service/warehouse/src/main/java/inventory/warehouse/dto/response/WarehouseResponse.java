package inventory.warehouse.dto.response;

public record WarehouseResponse(
        Long id,
        String name,
        String postcode,
        String baseAddress,
        String detailAddress,
        String managerName,
        String managerContact
) {
    public static WarehouseResponse of(
            final Long id,
            final String name,
            final String postcode,
            final String baseAddress,
            final String detailAddress,
            final String managerName,
            final String managerContact
    ) {
        return new WarehouseResponse(id, name, postcode, baseAddress, detailAddress, managerName, managerContact);
    }
}
