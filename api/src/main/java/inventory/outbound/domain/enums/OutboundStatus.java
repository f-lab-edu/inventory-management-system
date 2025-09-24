package inventory.outbound.domain.enums;

import lombok.Getter;

@Getter
public enum OutboundStatus {
    ORDERED("출고 등록"),
    PICKING("피킹 중"),
    SHIPPED("출고 완료"),
    CANCELED("출고 취소");

    private final String description;

    OutboundStatus(String description) {
        this.description = description;
    }
}
