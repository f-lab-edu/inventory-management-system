package inventory.inbound.repository;

import inventory.inbound.domain.Inbound;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboundRepository extends JpaRepository<Inbound, Long>, InboundQueryRepository {
}