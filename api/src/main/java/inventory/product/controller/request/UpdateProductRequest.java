package inventory.product.controller.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateProductRequest(
        @NotBlank(message = "상품명은 필수입니다")
        String productName,

        String thumbnailUrl
) {
}
