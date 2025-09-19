package inventory.outbound.service.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OutboundProductRequest(
        @NotNull(message = "상품 ID는 필수입니다")
        Long productId,

        @Positive(message = "출고 수량은 0보다 커야 합니다")
        int quantity
) {
}
