package inventory.outbound.repository;

import inventory.outbound.domain.OutboundProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboundProductRepository extends JpaRepository<OutboundProduct, Long> {
}
