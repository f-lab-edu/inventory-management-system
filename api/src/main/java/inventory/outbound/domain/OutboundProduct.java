package inventory.outbound.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE outbound_product SET deleted = true, deleted_at = NOW() WHERE outbound_product_id = ?")
@SQLRestriction("deleted = false and deleted_at is null")
@Getter
@Entity
public class OutboundProduct {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long outboundProductId;

    private Long outboundId;

    private Long productId;

    private int requestedQuantity;

    private boolean deleted = false;

    private LocalDateTime deletedAt;

    @Builder
    public OutboundProduct(Long outboundId, Long productId, int requestedQuantity) {
        this.outboundId = outboundId;
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
    }
}
