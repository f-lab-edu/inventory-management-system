package inventory.notification.domain.enums;

import inventory.notification.service.request.LowStockProduct;
import inventory.notification.service.request.RecipientInfo;
import java.util.List;
import lombok.Getter;

@Getter
public enum NotificationType {
    LOW_STOCK("안전재고 미만 알림") {
        @Override
        public String generateSubject() {
            return "[인벤토리] 안전재고 미만 상품이 있습니다.";
        }

        @Override
        public String generateContent(RecipientInfo recipient, List<LowStockProduct> products) {
            StringBuilder text = new StringBuilder();
            text.append("========================================\n");
            text.append("재고 부족 알림\n");
            text.append("========================================\n\n");

            text.append("안녕하세요, ").append(recipient.recipientName()).append(" 님\n\n");
            text.append("다음 상품들의 재고가 안전재고 미만으로 떨어졌습니다.\n");
            text.append("총 ").append(products.size()).append("개 상품의 재고 확인이 필요합니다.\n\n");

            text.append("========================================\n");
            text.append("상품 목록\n");
            text.append("========================================\n\n");

            for (LowStockProduct product : products) {
                int shortage = product.safetyStock() - product.currentStock();
                text.append("▶ ").append(product.productName()).append("\n");
                text.append("  - 현재 재고: ").append(product.currentStock()).append("\n");
                text.append("  - 안전 재고: ").append(product.safetyStock()).append("\n");
                text.append("  - 부족 수량: ").append(shortage).append("\n\n");
            }

            text.append("========================================\n\n");

            return text.toString();
        }
    };

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public abstract String generateSubject();

    public abstract String generateContent(RecipientInfo recipient, List<LowStockProduct> lowStockProducts);
}
