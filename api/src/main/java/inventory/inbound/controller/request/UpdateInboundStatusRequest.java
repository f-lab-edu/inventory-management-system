package inventory.inbound.controller.request;

import inventory.inbound.enums.InboundStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateInboundStatusRequest(
        @NotNull(message = "입고 상태는 필수입니다")
        InboundStatus status
) {
}
