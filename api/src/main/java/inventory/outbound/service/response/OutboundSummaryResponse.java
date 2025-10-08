package inventory.outbound.service.response;

import inventory.outbound.domain.enums.OutboundStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record OutboundSummaryResponse(
        Long outboundId,
        String orderNumber,
        Long warehouseId,
        String warehouseName,
        String recipientName,
        LocalDate requestedDate,
        LocalDate expectedDate,
        OutboundStatus status,
        LocalDateTime createdAt
) {
}
