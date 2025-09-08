package inventory.warehouse.repository;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.warehouse.domain.Warehouse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class WarehouseMemoryRepository implements WarehouseRepository {

    private final Map<Long, Warehouse> store = new ConcurrentHashMap<>();

    @Override
    public Warehouse save(Warehouse warehouse) {
        if (warehouse == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        return store.put(warehouse.getWarehouseId(), warehouse);
    }

    @Override
    public Optional<Warehouse> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Warehouse> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }
        store.remove(id);
    }
}
