package inventory.inbound.repository;

import inventory.inbound.service.query.InboundSearchCondition;
import inventory.inbound.service.response.InboundSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InboundQueryRepository {

    Page<InboundSummaryResponse> findInboundSummaries(
            InboundSearchCondition condition,
            Pageable pageable
    );
}
