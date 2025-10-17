package inventory.notification.domain.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
    LOW_STOCK("안전재고 미만 알림");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }
}