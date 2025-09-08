package inventory.inbound.controller.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record CreateInboundRequest(
        @NotNull(message = "창고 ID는 필수입니다")
        Long warehouseId,

        @NotNull(message = "공급업체 ID는 필수입니다")
        Long supplierId,

        @NotNull(message = "입고 예정일은 필수입니다")
        LocalDate expectedDate,

        @NotEmpty(message = "상품 목록은 비어있을 수 없습니다")
        @Valid
        List<InboundProductRequest> products
) {
}
