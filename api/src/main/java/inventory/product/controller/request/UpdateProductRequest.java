package inventory.product.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateProductRequest(
        @NotBlank(message = "상품명은 필수입니다")
        String productName,

        String thumbnailUrl,

        @NotNull(message = "상품 활성 상태는 필수입니다")
        boolean active
) {
}
