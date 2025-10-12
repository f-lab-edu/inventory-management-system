package inventory.notification.service.request;

public record LowStockProduct(
        String productName,
        int currentStock,
        int safetyStock
) {
}
