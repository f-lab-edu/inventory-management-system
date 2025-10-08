package inventory.inbound.service.query;

import inventory.inbound.domain.enums.InboundStatus;

import java.time.LocalDate;

public record InboundSearchCondition(
        Long warehouseId,
        Long supplierId,
        InboundStatus status,
        LocalDate startDate,
        LocalDate endDate
) {

    public static InboundSearchCondition of(Long warehouseId, Long supplierId, InboundStatus status) {
        return new InboundSearchCondition(
                warehouseId,
                supplierId,
                status,
                LocalDate.now(),
                LocalDate.now()
        );
    }

    public static InboundSearchCondition of(Long warehouseId, Long supplierId) {
        return new InboundSearchCondition(
                warehouseId,
                supplierId,
                null,
                LocalDate.now(),
                LocalDate.now()
        );
    }
}
