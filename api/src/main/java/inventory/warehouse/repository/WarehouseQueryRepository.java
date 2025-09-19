package inventory.warehouse.repository;

import inventory.warehouse.service.query.WarehouseSearchCondition;
import inventory.warehouse.service.response.WarehouseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WarehouseQueryRepository {

    Page<WarehouseResponse> findWarehouseSummaries(WarehouseSearchCondition condition, Pageable pageable);
}


