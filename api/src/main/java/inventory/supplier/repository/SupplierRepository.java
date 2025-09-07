package inventory.supplier.repository;

import inventory.supplier.domain.Supplier;
import java.util.List;
import java.util.Optional;

public interface SupplierRepository {
    Supplier save(Supplier supplier);

    Optional<Supplier> findById(Long id);

    List<Supplier> findAll();

    void deleteById(Long id);
}
