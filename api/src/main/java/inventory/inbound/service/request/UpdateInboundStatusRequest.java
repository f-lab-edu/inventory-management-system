package inventory.inbound.service.request;

import inventory.inbound.domain.enums.InboundStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateInboundStatusRequest(
        @NotNull(message = "입고 상태는 필수입니다")
        InboundStatus status
) {
}
