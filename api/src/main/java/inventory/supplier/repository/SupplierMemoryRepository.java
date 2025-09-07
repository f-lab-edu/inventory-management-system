package inventory.supplier.repository;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.supplier.domain.Supplier;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class SupplierMemoryRepository implements SupplierRepository {

    private final Map<Long, Supplier> store = new ConcurrentHashMap<>();

    @Override
    public Supplier save(Supplier supplier) {
        if (supplier == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        return store.put(supplier.getSupplierId(), supplier);
    }

    @Override
    public Optional<Supplier> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Supplier> findAll() {
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
