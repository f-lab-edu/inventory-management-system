package inventory.inbound.domain.enums;

import lombok.Getter;

@Getter
public enum InboundStatus {
    REGISTERED("입고 등록"),
    INSPECTING("검수 중"),
    COMPLETED("입고 완료"),
    REJECTED("입고 거절"),
    CANCELED("입고 취소");

    private final String description;

    InboundStatus(String description) {
        this.description = description;
    }
}
