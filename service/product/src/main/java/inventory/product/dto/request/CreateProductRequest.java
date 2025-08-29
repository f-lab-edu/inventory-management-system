package inventory.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateProductRequest(
        @NotBlank(message = "상품명은 필수입니다")
        @Size(max = 100, message = "상품명은 100자 이하여야 합니다")
        String name,

        @NotBlank(message = "상품 코드는 필수입니다")
        @Size(max = 50, message = "상품 코드는 50자 이하여야 합니다")
        String productCode,

        @NotBlank(message = "상품 설명은 필수입니다")
        @Size(max = 500, message = "상품 설명은 500자 이하여야 합니다")
        String description,

        @NotNull(message = "공급업체 ID는 필수입니다")
        @Positive(message = "공급업체 ID는 양수여야 합니다")
        Long supplierId,

        @NotNull(message = "가격은 필수입니다")
        @Positive(message = "가격은 양수여야 합니다")
        Long price,

        @Size(max = 20, message = "카테고리는 20자 이하여야 합니다")
        String category
) {
}
