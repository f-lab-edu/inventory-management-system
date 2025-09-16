package inventory.inbound.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class InboundProduct {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long inboundProductId;

    Long productId;

    Long inboundId;

    int quantity;

    @Builder
    public InboundProduct(Long productId, Long inboundId, int quantity) {
        this.productId = productId;
        this.inboundId = inboundId;
        this.quantity = quantity;
    }
}
