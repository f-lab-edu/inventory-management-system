package inventory.inbound.controller.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InboundProductRequest(
        @NotNull(message = "상품 ID는 필수입니다")
        Long productId,

        @Positive(message = "입고 수량은 0보다 커야 합니다")
        Integer quantity
) {
}
