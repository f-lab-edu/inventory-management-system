package inventory.outbound.service.response;

public record StockSummary(
        int totalProductCount,
        int lowStockProductCount,
        boolean hasInsufficientStock
) {
}
