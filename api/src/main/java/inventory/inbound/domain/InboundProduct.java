package inventory.inbound.domain;

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
@SQLDelete(sql = "UPDATE inbound_product SET deleted = true, deleted_at = NOW() WHERE inbound_product_id = ?")
@SQLRestriction("deleted = false and deleted_at is null")
@Getter
@Entity
public class InboundProduct {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long inboundProductId;

    private Long productId;

    private Long inboundId;

    private int quantity;

    private boolean deleted = false;

    private LocalDateTime deletedAt;

    @Builder
    public InboundProduct(Long productId, Long inboundId, int quantity) {
        this.productId = productId;
        this.inboundId = inboundId;
        this.quantity = quantity;
    }
}
