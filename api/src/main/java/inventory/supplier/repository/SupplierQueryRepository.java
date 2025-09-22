package inventory.supplier.repository;

import inventory.supplier.service.query.SupplierSearchCondition;
import inventory.supplier.service.response.SupplierResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupplierQueryRepository {

    Page<SupplierResponse> findSupplierSummaries(SupplierSearchCondition condition, Pageable pageable);
}


