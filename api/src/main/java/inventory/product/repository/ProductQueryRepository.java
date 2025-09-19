package inventory.product.repository;

import inventory.product.service.query.ProductSearchCondition;
import inventory.product.service.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductQueryRepository {

    Page<ProductResponse> findProductSummaries(ProductSearchCondition condition, Pageable pageable);
}


