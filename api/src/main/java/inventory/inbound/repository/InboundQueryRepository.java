package inventory.inbound.repository;

import inventory.inbound.domain.Inbound;
import inventory.inbound.domain.enums.InboundStatus;
import inventory.inbound.service.response.InboundSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface InboundQueryRepository {

    Page<Inbound> findInboundsWithConditions(
            InboundSearchCondition condition,
            Pageable pageable
    );

    Page<InboundSummaryResponse> findInboundSummaries(
            InboundSearchCondition condition,
            Pageable pageable
    );

    record InboundSearchCondition(
            Long warehouseId,
            Long supplierId,
            InboundStatus status,
            LocalDate startDate,
            LocalDate endDate
    ) {}
}
