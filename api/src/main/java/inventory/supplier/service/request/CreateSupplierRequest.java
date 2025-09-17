package inventory.supplier.service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateSupplierRequest(
        @NotBlank(message = "업체명은 필수입니다")
        String name,

        @NotBlank(message = "사업자등록번호는 필수입니다")
        @Pattern(regexp = "^\\d{10}$", message = "사업자등록번호는 10자리 숫자여야 합니다")
        String businessRegistrationNumber,

        @NotBlank(message = "우편번호는 필수입니다")
        @Pattern(regexp = "^\\d{5}$", message = "우편번호는 5자리 숫자여야 합니다")
        String postcode,

        @NotBlank(message = "기본주소는 필수입니다")
        @Size(max = 200, message = "기본주소는 200자 이하여야 합니다")
        String baseAddress,

        @Size(max = 100, message = "상세주소는 100자 이하여야 합니다")
        String detailAddress,

        @NotBlank(message = "대표자명은 필수입니다")
        String ceoName,

        @NotBlank(message = "담당자명은 필수입니다")
        String managerName,

        @NotBlank(message = "담당자 연락처는 필수입니다")
        String managerContact
) {
}
