package inventory.outbound.service.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.List;

public record CreateOutboundRequest(
        @NotNull(message = "창고 ID는 필수입니다")
        Long warehouseId,

        @NotNull(message = "출고 요청일은 필수입니다")
        LocalDate requestedDate,

        @NotBlank(message = "수령인 이름은 필수입니다")
        String recipientName,

        @NotBlank(message = "수령인 연락처는 필수입니다")
        String recipientContact,

        @NotBlank(message = "우편번호는 필수입니다")
        @Pattern(regexp = "^\\d{5}$", message = "우편번호는 5자리 숫자여야 합니다")
        String deliveryPostcode,

        @NotBlank(message = "기본주소는 필수입니다")
        String deliveryBaseAddress,

        @NotBlank(message = "상세주소는 필수입니다")
        String deliveryDetailAddress,

        String deliveryMemo,

        @NotEmpty(message = "출고 상품 목록은 비어있을 수 없습니다")
        @Valid
        List<OutboundProductRequest> products
) {
}
