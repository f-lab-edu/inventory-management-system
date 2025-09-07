package inventory.inbound.repository;

import inventory.inbound.domain.Inbound;

import java.util.List;
import java.util.Optional;

public interface InboundRepository {

    Inbound save(Inbound inbound);

    Optional<Inbound> findById(Long id);

    List<Inbound> findAll();

    void deleteById(Long id);
}