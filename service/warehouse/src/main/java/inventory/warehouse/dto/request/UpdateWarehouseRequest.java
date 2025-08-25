package inventory.warehouse.dto.request;

public record UpdateWarehouseRequest(
        String name,
        String postcode,
        String baseAddress,
        String detailAddress,
        String managerName,
        String managerContact
) {
}
