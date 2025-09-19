package inventory.outbound.domain;

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
public class OutboundProduct {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long outboundProductId;

    private Long outboundId;

    private Long productId;

    private int requestedQuantity;

    @Builder
    public OutboundProduct(Long outboundId, Long productId, int requestedQuantity) {
        this.outboundId = outboundId;
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
    }
}
