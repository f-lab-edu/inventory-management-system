package inventory.inbound.domain;

import inventory.inbound.domain.enums.InboundStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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

    private InboundStatus status;

    @Builder
    public Inbound(Long warehouseId, Long supplierId, LocalDate expectedDate, InboundStatus status) {
        this.warehouseId = warehouseId;
        this.supplierId = supplierId;
        this.expectedDate = expectedDate;
        this.status = status != null ? status : InboundStatus.REGISTERED;
    }

    public void updateStatus(InboundStatus newStatus) {
        this.status = newStatus;
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