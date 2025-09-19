package inventory.outbound.repository;

import inventory.outbound.domain.Outbound;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboundRepository extends JpaRepository<Outbound, Long> {
}
