package inventory.notification.service.request;

import java.util.List;

public record LowStockNotiRequest(
        RecipientInfo recipient,
        List<LowStockProduct> products
) {
}
