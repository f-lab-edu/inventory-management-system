package inventory.inbound.repository;

import inventory.inbound.domain.Inbound;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InboundMemoryRepository implements InboundRepository {

    private final Map<Long, Inbound> store = new ConcurrentHashMap<>();

    @Override
    public Inbound save(Inbound inbound) {
        store.put(inbound.getInboundId(), inbound);
        return inbound;
    }

    @Override
    public Optional<Inbound> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Inbound> findAll() {
        return store.values().stream().toList();
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}
