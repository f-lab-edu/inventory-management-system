package inventory.product.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductRequest(
        @NotNull(message = "공급업체 ID는 필수입니다")
        Long supplierId,

        @NotBlank(message = "상품명은 필수입니다")
        String productName,

        @NotBlank(message = "상품코드는 필수입니다")
        String productCode,

        @NotBlank(message = "상품단위는 필수입니다")
        String unit,

        String thumbnailUrl
) {
}
