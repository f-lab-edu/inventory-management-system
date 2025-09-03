package inventory.warehouse.repository;

import inventory.warehouse.domain.Warehouse;

import java.util.List;
import java.util.Optional;

public interface WarehouseRepository {
    Warehouse save(Warehouse warehouse);

    Optional<Warehouse> findById(Long id);

    List<Warehouse> findAll();

    void deleteById(Long id);
}
