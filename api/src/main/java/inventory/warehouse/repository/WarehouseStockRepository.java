package inventory.warehouse.repository;

import inventory.warehouse.domain.WarehouseStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, Long> {

    Optional<WarehouseStock> findByWarehouseIdAndProductId(Long warehouseId, Long productId);
    
    List<WarehouseStock> findByWarehouseIdAndProductIdIn(Long warehouseId, List<Long> productIds);
}
