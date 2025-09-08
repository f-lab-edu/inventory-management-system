package inventory.inbound.domain;

import inventory.inbound.enums.InboundStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Getter
public class Inbound {

    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    private Long inboundId;

    private Long warehouseId;

    private Long supplierId;

    private LocalDate expectedDate;

    private List<InboundProduct> products;

    private InboundStatus status;

    @Builder
    public Inbound(Long inboundId, Long warehouseId, Long supplierId, LocalDate expectedDate,
                   List<InboundProduct> products, InboundStatus status) {
        this.inboundId = inboundId != null ? inboundId : ID_GENERATOR.getAndIncrement();
        this.warehouseId = warehouseId;
        this.supplierId = supplierId;
        this.expectedDate = expectedDate;
        this.products = products;
        this.status = status != null ? status : InboundStatus.REGISTERED;
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