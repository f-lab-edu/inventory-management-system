package inventory.warehouse.repository;

import inventory.warehouse.domain.Warehouse;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class WarehouseMemoryRepository implements WarehouseRepository {
    @Override
    public Warehouse save(Warehouse warehouse) {
        return null;
    }

    @Override
    public Optional<Warehouse> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Warehouse> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(Long id) {

    }
}
