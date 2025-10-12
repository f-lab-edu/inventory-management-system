package inventory.notification.service;

import inventory.notification.service.request.LowStockNotiRequest;
import inventory.notification.service.request.LowStockProduct;
import inventory.notification.service.request.RecipientInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    public void notifyLowStock(LowStockNotiRequest lowStockNotiRequest) {
        RecipientInfo recipient = lowStockNotiRequest.recipient();

        log.warn("========== 재고 부족 알림 ==========");
        log.warn("수신자: {} ({})", recipient.recipientName(), recipient.recipientContact());
        log.warn("총 {}개 상품의 재고가 안전재고 미만입니다.", lowStockNotiRequest.products().size());

        for (LowStockProduct product : lowStockNotiRequest.products()) {
            log.warn("상품: {}, 현재재고: {}, 안전재고: {}",
                    product.productName(),
                    product.currentStock(),
                    product.safetyStock());
        }

        log.warn("=====================================");
    }
}
