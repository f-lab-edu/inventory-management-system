package inventory.notification.service;

import inventory.notification.domain.Notification;
import inventory.notification.domain.enums.NotificationType;
import inventory.notification.repository.NotificationRepository;
import inventory.notification.service.request.LowStockProduct;
import inventory.notification.service.request.RecipientInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class NotificationServiceIntegrationTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @DisplayName("실제 메일 전송 테스트 - 재고 부족 알림")
    @Test
    void sendLowStockNotification_실제메일전송() {
        // given
        RecipientInfo recipient = new RecipientInfo(
                "테스트 사용자",
                "01012345678",
                "hwasuyong99@naver.com"
        );

        List<LowStockProduct> products = List.of(
                new LowStockProduct("테스트 상품 A", 5, 20),
                new LowStockProduct("테스트 상품 B", 3, 15),
                new LowStockProduct("테스트 상품 C", 1, 10)
        );

        // when
        notificationService.sendLowStockNotification(recipient, products);

        // then
        // DB에 알림 이력이 저장되었는지 확인
        List<Notification> notifications = notificationRepository.findAll();
        assertThat(notifications).hasSize(1);

        Notification savedNotification = notifications.get(0);
        assertThat(savedNotification.getRecipientName()).isEqualTo("테스트 사용자");
        assertThat(savedNotification.getRecipientEmail()).isEqualTo("hwasuyong99@naver.com");
        assertThat(savedNotification.getNotificationType()).isEqualTo(NotificationType.LOW_STOCK);
        assertThat(savedNotification.getSendAt()).isNotNull();

        System.out.println("메일 전송 완료");
    }

    @Test
    @DisplayName("단일 상품 재고 부족 알림 테스트")
    void sendLowStockNotification_단일상품() {
        // given
        RecipientInfo recipient = new RecipientInfo(
                "단일 상품 테스트",
                "01098765432",
                "hwasuyong99@naver.com"
        );

        List<LowStockProduct> products = List.of(
                new LowStockProduct("단일 테스트 상품", 2, 10)
        );

        // when
        notificationService.sendLowStockNotification(recipient, products);

        // then
        List<Notification> notifications = notificationRepository.findAll();
        assertThat(notifications).hasSize(1);

        System.out.println("메일 전송 완료");
    }

    @Test
    @DisplayName("대량 상품 재고 부족 알림 테스트")
    void sendLowStockNotification_대량상품() {
        // given
        RecipientInfo recipient = new RecipientInfo(
                "대량 상품 테스트",
                "01011112222",
                "hwasuyong99@naver.com"
        );

        List<LowStockProduct> products = List.of(
                new LowStockProduct("대량 상품 1", 1, 20),
                new LowStockProduct("대량 상품 2", 2, 15),
                new LowStockProduct("대량 상품 3", 3, 25),
                new LowStockProduct("대량 상품 4", 4, 30),
                new LowStockProduct("대량 상품 5", 5, 35)
        );

        // when
        notificationService.sendLowStockNotification(recipient, products);

        // then
        List<Notification> notifications = notificationRepository.findAll();
        assertThat(notifications).hasSize(1);

        System.out.println("메일 전송 완료");
    }
}
