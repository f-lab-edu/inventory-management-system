package inventory.outbound.repository;

import inventory.outbound.service.query.OutboundSearchCondition;
import inventory.outbound.service.response.OutboundSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OutboundQueryRepository {

    Page<OutboundSummaryResponse> findOutboundSummaries(
            OutboundSearchCondition condition,
            Pageable pageable
    );
}
