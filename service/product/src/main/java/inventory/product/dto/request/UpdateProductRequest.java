package inventory.product.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateProductRequest(
        @Size(max = 100, message = "상품명은 100자 이하여야 합니다")
        String name,

        @Size(max = 50, message = "상품 코드는 50자 이하여야 합니다")
        String productCode,

        @Size(max = 500, message = "상품 설명은 500자 이하여야 합니다")
        String description,

        @Positive(message = "공급업체 ID는 양수여야 합니다")
        Long supplierId,

        @Positive(message = "가격은 양수여야 합니다")
        Long price,

        @Size(max = 20, message = "카테고리는 20자 이하여야 합니다")
        String category,

        @Positive(message = "안전재고는 양수여야 합니다")
        Long safetyStock
) {
}
