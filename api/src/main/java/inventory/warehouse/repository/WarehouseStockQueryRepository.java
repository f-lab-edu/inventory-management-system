package inventory.warehouse.repository;

import inventory.warehouse.service.query.WarehouseStockSearchCondition;
import inventory.warehouse.service.response.WarehouseStockResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WarehouseStockQueryRepository {

    Page<WarehouseStockResponse> findWarehouseStockSummaries(WarehouseStockSearchCondition condition, Pageable pageable);
}


