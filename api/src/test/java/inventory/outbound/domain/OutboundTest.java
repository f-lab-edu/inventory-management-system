package inventory.outbound.domain;

import inventory.outbound.domain.enums.OutboundStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OutboundTest {

    @DisplayName("출고 생성 시 기본값이 올바르게 설정된다")
    @Test
    void createOutboundWithDefaultValues() {
        // given
        Long warehouseId = 1L;
        String recipientName = "홍길동";
        LocalDate requestedDate = LocalDate.now();

        // when
        Outbound outbound = Outbound.builder()
                .warehouseId(warehouseId)
                .recipientName(recipientName)
                .recipientContact("01012345678")
                .deliveryPostcode("12345")
                .deliveryBaseAddress("서울시 강남구")
                .deliveryDetailAddress("테헤란로 123")
                .requestedDate(requestedDate)
                .deliveryMemo("문 앞에 놓아주세요")
                .build();

        // then
        assertThat(outbound.getOutboundStatus()).isEqualTo(OutboundStatus.ORDERED);
        assertThat(outbound.getOrderNumber()).isNotNull();
        assertThat(outbound.getOrderNumber()).startsWith("OB");
        assertThat(outbound.getExpectedDate()).isNotNull();
    }

    @DisplayName("출고 생성 시 주문번호를 명시적으로 지정할 수 있다")
    @Test
    void createOutboundWithExplicitOrderNumber() {
        // given
        String orderNumber = "OB20241201-ABC12345";

        // when
        Outbound outbound = Outbound.builder()
                .warehouseId(1L)
                .recipientName("홍길동")
                .recipientContact("01012345678")
                .deliveryPostcode("12345")
                .deliveryBaseAddress("서울시 강남구")
                .deliveryDetailAddress("테헤란로 123")
                .requestedDate(LocalDate.now())
                .orderNumber(orderNumber)
                .build();

        // then
        assertThat(outbound.getOrderNumber()).isEqualTo(orderNumber);
    }

    @DisplayName("출고 생성 시 상태를 명시적으로 지정할 수 있다")
    @Test
    void createOutboundWithExplicitStatus() {
        // given
        OutboundStatus status = OutboundStatus.PICKING;

        // when
        Outbound outbound = Outbound.builder()
                .warehouseId(1L)
                .recipientName("홍길동")
                .recipientContact("01012345678")
                .deliveryPostcode("12345")
                .deliveryBaseAddress("서울시 강남구")
                .deliveryDetailAddress("테헤란로 123")
                .requestedDate(LocalDate.now())
                .outboundStatus(status)
                .build();

        // then
        assertThat(outbound.getOutboundStatus()).isEqualTo(status);
    }

    @DisplayName("출고 상태 전환이 올바르게 동작한다")
    @ParameterizedTest
    @MethodSource("validStatusTransitions")
    void updateStatusWithValidTransitions(OutboundStatus fromStatus, OutboundStatus toStatus) {
        // given
        Outbound outbound = createOutboundWithStatus(fromStatus);

        // when
        Outbound updatedOutbound = outbound.updateStatus(toStatus);

        // then
        assertThat(updatedOutbound.getOutboundStatus()).isEqualTo(toStatus);
    }

    @DisplayName("출고 상태를 SHIPPED로 변경할 때 출고일이 설정된다")
    @Test
    void updateStatusToShippedSetsShippedDate() {
        // given
        Outbound outbound = createOutboundWithStatus(OutboundStatus.PICKING);
        LocalDate beforeShippedDate = outbound.getShippedDate();

        // when
        Outbound updatedOutbound = outbound.updateStatus(OutboundStatus.SHIPPED);

        // then
        assertThat(updatedOutbound.getOutboundStatus()).isEqualTo(OutboundStatus.SHIPPED);
        assertThat(updatedOutbound.getShippedDate()).isEqualTo(LocalDate.now());
        assertThat(updatedOutbound.getShippedDate()).isNotEqualTo(beforeShippedDate);
    }

    private static Stream<Arguments> validStatusTransitions() {
        return Stream.of(
                Arguments.of(OutboundStatus.ORDERED, OutboundStatus.PICKING),
                Arguments.of(OutboundStatus.ORDERED, OutboundStatus.CANCELED),
                Arguments.of(OutboundStatus.PICKING, OutboundStatus.SHIPPED),
                Arguments.of(OutboundStatus.PICKING, OutboundStatus.CANCELED)
        );
    }

    @DisplayName("잘못된 출고 상태 전환 시 예외가 발생한다")
    @ParameterizedTest
    @MethodSource("invalidStatusTransitions")
    void updateStatusWithInvalidTransitions(OutboundStatus fromStatus, OutboundStatus toStatus, String expectedMessage) {
        // given
        Outbound outbound = createOutboundWithStatus(fromStatus);

        // when & then
        assertThatThrownBy(() -> outbound.updateStatus(toStatus))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(expectedMessage);
    }

    private static Stream<Arguments> invalidStatusTransitions() {
        return Stream.of(
                Arguments.of(OutboundStatus.ORDERED, OutboundStatus.SHIPPED, "출고 등록 상태에서는 피킹 중 또는 취소로만 변경 가능합니다"),
                Arguments.of(OutboundStatus.PICKING, OutboundStatus.ORDERED, "피킹 중 상태에서는 출고 완료 또는 취소로만 변경 가능합니다"),
                Arguments.of(OutboundStatus.SHIPPED, OutboundStatus.PICKING, "출고 완료 상태에서는 더 이상 상태 변경이 불가능합니다"),
                Arguments.of(OutboundStatus.CANCELED, OutboundStatus.PICKING, "알 수 없는 상태입니다")
        );
    }

    @DisplayName("출고가 취소 가능한 상태인지 확인할 수 있다")
    @ParameterizedTest
    @MethodSource("cancelableStatuses")
    void canBeCanceled(OutboundStatus status, boolean expected) {
        // given
        Outbound outbound = createOutboundWithStatus(status);

        // when & then
        assertThat(outbound.canBeCanceled()).isEqualTo(expected);
    }

    private static Stream<Arguments> cancelableStatuses() {
        return Stream.of(
                Arguments.of(OutboundStatus.ORDERED, true),
                Arguments.of(OutboundStatus.PICKING, true),
                Arguments.of(OutboundStatus.SHIPPED, false),
                Arguments.of(OutboundStatus.CANCELED, false)
        );
    }

    @DisplayName("예상 출고일 계산이 올바르게 동작한다")
    @ParameterizedTest
    @MethodSource("expectedDateCalculationData")
    void calculateExpectedDate(LocalDate requestedDate, LocalDate currentDate, LocalTime currentTime, LocalDate expectedResult) {
        // given
        Outbound outbound = Outbound.builder()
                .warehouseId(1L)
                .recipientName("홍길동")
                .recipientContact("01012345678")
                .deliveryPostcode("12345")
                .deliveryBaseAddress("서울시 강남구")
                .deliveryDetailAddress("테헤란로 123")
                .requestedDate(requestedDate)
                .build();

        // when
        LocalDate result = outbound.calculateExpectedDate(currentDate, currentTime);

        // then
        assertThat(result).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> expectedDateCalculationData() {
        LocalDate today = LocalDate.of(2025, 9, 25);
        LocalDate tomorrow = today.plusDays(1);
        LocalDate yesterday = today.minusDays(1);

        return Stream.of(
                // 당일 요청 - 컷오프 시간 전 (10시 전)
                Arguments.of(today, today, LocalTime.of(9, 0), today),

                // 당일 요청 - 컷오프 시간 후 (10시 후)
                Arguments.of(today, today, LocalTime.of(11, 0), tomorrow),

                // 당일 요청 - 컷오프 시간 정확히 (10시)
                Arguments.of(today, today, LocalTime.of(10, 0), tomorrow),

                // 내일 요청
                Arguments.of(tomorrow, today, LocalTime.of(9, 0), tomorrow),
                Arguments.of(tomorrow, today, LocalTime.of(11, 0), tomorrow),

                // 어제 요청 (과거 날짜)
                Arguments.of(yesterday, today, LocalTime.of(9, 0), yesterday),
                Arguments.of(yesterday, today, LocalTime.of(11, 0), yesterday)
        );
    }

    private Outbound createOutboundWithStatus(OutboundStatus status) {
        return Outbound.builder()
                .warehouseId(1L)
                .recipientName("홍길동")
                .recipientContact("01012345678")
                .deliveryPostcode("12345")
                .deliveryBaseAddress("서울시 강남구")
                .deliveryDetailAddress("테헤란로 123")
                .requestedDate(LocalDate.now())
                .outboundStatus(status)
                .build();
    }

}
