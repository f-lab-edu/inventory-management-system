package inventory.outbound.service.query;

import inventory.outbound.domain.enums.OutboundStatus;

import java.time.LocalDate;

public record OutboundSearchCondition(
        String orderNumber,
        Long warehouseId,
        OutboundStatus status,
        LocalDate startDate,
        LocalDate endDate
) {

    public static OutboundSearchCondition of(String orderNumber, Long warehouseId) {
        return new OutboundSearchCondition(
                orderNumber,
                warehouseId,
                null,
                LocalDate.now(),
                LocalDate.now()
        );
    }

    public static OutboundSearchCondition of(String orderNumber, Long warehouseId, OutboundStatus status) {
        return new OutboundSearchCondition(
                orderNumber,
                warehouseId,
                status,
                LocalDate.now(),
                LocalDate.now()
        );
    }
}
