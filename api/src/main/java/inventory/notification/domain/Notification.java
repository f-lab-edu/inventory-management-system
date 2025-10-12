package inventory.notification.domain;

import inventory.notification.domain.enums.NotificationType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Notification {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long notificationId;

    private String recipientName;

    private String recipientEmail;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private LocalDateTime sendAt;

    @Builder
    public Notification(String recipientName, String recipientEmail,
                        NotificationType notificationType) {
        this.recipientName = recipientName;
        this.recipientEmail = recipientEmail;
        this.notificationType = notificationType;
        this.sendAt = LocalDateTime.now();
    }
}
