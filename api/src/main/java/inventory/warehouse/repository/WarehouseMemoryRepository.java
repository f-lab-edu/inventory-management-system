package inventory.warehouse.repository;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.warehouse.domain.Warehouse;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class WarehouseMemoryRepository implements WarehouseRepository {

    private final Map<Long, Warehouse> store = new ConcurrentHashMap<>();
    private final AtomicLong autoIncrementId = new AtomicLong();

    @Override
    public Warehouse save(Warehouse warehouse) {
        if (warehouse == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        long key = warehouse.getWarehouseId() == null ? this.autoIncrementId.getAndIncrement() : warehouse.getWarehouseId();

        return store.put(key, warehouse);
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
