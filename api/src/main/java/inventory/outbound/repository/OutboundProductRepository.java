package inventory.outbound.repository;

import inventory.outbound.domain.OutboundProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboundProductRepository extends JpaRepository<OutboundProduct, Long> {
    List<OutboundProduct> findByOutboundId(Long outboundId);
}
