package inventory.notification.service.util;

import inventory.notification.service.request.LowStockProduct;
import inventory.notification.service.request.RecipientInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MailTemplateGenerator {

    public String generateLowStockSubject() {
        return "[인벤토리] 안전재고 미만 상품이 있습니다.";
    }

    public String generateLowStockContent(RecipientInfo recipient, List<LowStockProduct> products) {
        StringBuilder content = new StringBuilder();
        content.append("========================================\n");
        content.append("재고 부족 알림\n");
        content.append("========================================\n\n");

        content.append("안녕하세요, ").append(recipient.recipientName()).append(" 님\n\n");
        content.append("다음 상품들의 재고가 안전재고 미만으로 떨어졌습니다.\n");
        content.append("총 ").append(products.size()).append("개 상품의 재고 확인이 필요합니다.\n\n");

        content.append("========================================\n");
        content.append("상품 목록\n");
        content.append("========================================\n\n");

        for (LowStockProduct product : products) {
            int shortage = product.safetyStock() - product.currentStock();
            content.append("▶ ").append(product.productName()).append("\n");
            content.append("  - 현재 재고: ").append(product.currentStock()).append("\n");
            content.append("  - 안전 재고: ").append(product.safetyStock()).append("\n");
            content.append("  - 부족 수량: ").append(shortage).append("\n\n");
        }

        content.append("========================================\n\n");
        return content.toString();
    }
}
