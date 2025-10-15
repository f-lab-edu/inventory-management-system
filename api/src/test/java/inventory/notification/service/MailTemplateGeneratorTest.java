package inventory.notification.service;

import inventory.notification.service.request.LowStockProduct;
import inventory.notification.service.request.RecipientInfo;
import inventory.notification.service.util.MailTemplateGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MailTemplateGeneratorTest {

    @Autowired
    private MailTemplateGenerator mailTemplateGenerator;

    @Test
    @DisplayName("재고 부족 알림 제목 생성 테스트")
    void generateLowStockSubject() {
        // when
        String subject = mailTemplateGenerator.generateLowStockSubject();

        // then
        assertThat(subject).isEqualTo("[인벤토리] 안전재고 미만 상품이 있습니다.");
    }

    @Test
    @DisplayName("재고 부족 알림 내용 생성 테스트")
    void generateLowStockContent() {
        // given
        RecipientInfo recipient = new RecipientInfo(
                "홍길동",
                "010-1234-5678",
                "hwasuyong99@naver.com"
        );

        List<LowStockProduct> products = List.of(
                new LowStockProduct("상품A", 5, 10),
                new LowStockProduct("상품B", 3, 15)
        );

        // when
        String content = mailTemplateGenerator.generateLowStockContent(recipient, products);

        // then
        assertThat(content).contains("홍길동 님");
        assertThat(content).contains("총 2개 상품의 재고 확인이 필요합니다");
        assertThat(content).contains("상품A");
        assertThat(content).contains("상품B");
        assertThat(content).contains("현재 재고: 5");
        assertThat(content).contains("안전 재고: 10");
        assertThat(content).contains("부족 수량: 5");
    }

    @Test
    @DisplayName("빈 상품 목록으로 재고 부족 알림 내용 생성 테스트")
    void generateLowStockContentWithEmptyProducts() {
        // given
        RecipientInfo recipient = new RecipientInfo(
                "홍길동",
                "010-1234-5678",
                "hwasuyong99@naver.com"
        );

        List<LowStockProduct> products = List.of();

        // when
        String content = mailTemplateGenerator.generateLowStockContent(recipient, products);

        // then
        assertThat(content).contains("홍길동 님");
        assertThat(content).contains("총 0개 상품의 재고 확인이 필요합니다");
    }
}