package inventory.notification.service.request;

public record RecipientInfo(
        String recipientName,
        String recipientContact,
        String recipientEmail
) {
}
