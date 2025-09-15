package inventory.inbound.domain;

import inventory.inbound.enums.InboundStatus;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Inbound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inboundId;

    private Long warehouseId;

    private Long supplierId;

    private LocalDate expectedDate;

    @ElementCollection
    private List<InboundProduct> products;

    private InboundStatus status;

    @Builder
    public Inbound(Long warehouseId, Long supplierId, LocalDate expectedDate,
                   List<InboundProduct> products, InboundStatus status) {
        this.warehouseId = warehouseId;
        this.supplierId = supplierId;
        this.expectedDate = expectedDate;
        this.products = products;
        this.status = status != null ? status : InboundStatus.REGISTERED;
    }

    public Inbound updateStatus(Inbound newInbound) {
        this.status = newInbound.status;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Inbound inbound = (Inbound) o;
        return Objects.equals(inboundId, inbound.inboundId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inboundId);
    }
}