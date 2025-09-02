package inventory.inbound.enums;

import lombok.Getter;

@Getter
public enum InboundStatus {
    REGISTERED("입고 등록"),
    INSPECTING("검수 중"),
    COMPLETED("입고 완료");

    private final String description;

    InboundStatus(String description) {
        this.description = description;
    }

}
