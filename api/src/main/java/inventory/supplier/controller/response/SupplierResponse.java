package inventory.supplier.controller.response;

import inventory.supplier.domain.Supplier;
import java.time.LocalDateTime;

public record SupplierResponse(
        Long id,
        String name,
        String businessRegistrationNumber,
        String postcode,
        String baseAddress,
        String detailAddress,
        String ceoName,
        String managerName,
        String managerContact,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static SupplierResponse of(
            final Long id,
            final String name,
            final String businessRegistrationNumber,
            final String postcode,
            final String baseAddress,
            final String detailAddress,
            final String ceoName,
            final String managerName,
            final String managerContact,
            final LocalDateTime createdAt,
            final LocalDateTime modifiedAt
    ) {
        return new SupplierResponse(
                id, name, businessRegistrationNumber,
                postcode, baseAddress, detailAddress,
                ceoName, managerName, managerContact,
                createdAt, modifiedAt
        );
    }

    public static SupplierResponse from(Supplier supplier) {
        return new SupplierResponse(
                supplier.getSupplierId(),
                supplier.getName(),
                supplier.getBusinessRegistrationNumber(),
                supplier.getPostcode(),
                supplier.getBaseAddress(),
                supplier.getDetailAddress(),
                supplier.getCeoName(),
                supplier.getManagerName(),
                supplier.getManagerContact(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
