package inventory.inbound.repository;

import inventory.inbound.domain.InboundProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InboundProductRepository extends JpaRepository<InboundProduct, Long> {
    List<InboundProduct> findInboundProductsByInboundId(Long id);
}
