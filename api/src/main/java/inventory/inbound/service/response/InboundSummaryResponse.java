package inventory.inbound.service.response;

import inventory.inbound.domain.enums.InboundStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record InboundSummaryResponse(
        Long inboundId,
        Long warehouseId,
        String warehouseName,
        Long supplierId,
        String supplierName,
        LocalDate expectedDate,
        InboundStatus status,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
}
