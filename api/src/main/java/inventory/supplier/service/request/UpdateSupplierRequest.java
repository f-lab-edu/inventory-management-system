package inventory.supplier.service.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateSupplierRequest(

        @Pattern(regexp = "^\\d{5}$", message = "우편번호는 5자리 숫자여야 합니다")
        String postcode,

        @Size(max = 100, message = "기본주소는 100자 이하여야 합니다")
        String baseAddress,

        @Size(max = 100, message = "상세주소는 100자 이하여야 합니다")
        String detailAddress,

        @Size(max = 30, message = "대표자명은 50자 이하여야 합니다")
        String ceoName,

        @Size(max = 30, message = "담당자명은 20자 이하여야 합니다")
        String managerName,

        @Size(max = 30, message = "담당자명은 20자 이하여야 합니다")
        String managerContact
) {
}
