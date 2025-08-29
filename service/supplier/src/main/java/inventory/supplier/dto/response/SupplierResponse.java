package inventory.supplier.dto.response;

public record SupplierResponse(
        Long id,
        String name,
        String businessRegistrationNumber,
        String ceoName,
        String postcode,
        String baseAddress,
        String detailAddress,
        String contact
) {
    public static SupplierResponse of(
            final Long id,
            final String name,
            final String businessRegistrationNumber,
            final String ceoName,
            final String postcode,
            final String baseAddress,
            final String detailAddress,
            final String contact
    ) {
        return new SupplierResponse(id, name, businessRegistrationNumber, ceoName, postcode, baseAddress, detailAddress, contact);
    }
}
