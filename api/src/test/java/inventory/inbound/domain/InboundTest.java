package inventory.inbound.domain;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.inbound.domain.enums.InboundStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
class InboundTest {

    @DisplayName("입고 생성 시 기본값이 올바르게 설정된다")
    @Test
    void createInboundWithDefaultValues() {
        // given
        Long warehouseId = 1L;
        Long supplierId = 1L;
        LocalDate expectedDate = LocalDate.now();

        // when
        Inbound inbound = Inbound.builder()
                .warehouseId(warehouseId)
                .supplierId(supplierId)
                .expectedDate(expectedDate)
                .build();

        // then
        assertThat(inbound.getWarehouseId()).isEqualTo(warehouseId);
        assertThat(inbound.getSupplierId()).isEqualTo(supplierId);
        assertThat(inbound.getExpectedDate()).isEqualTo(expectedDate);
        assertThat(inbound.getStatus()).isEqualTo(InboundStatus.REGISTERED);
        assertThat(inbound.getCreatedAt()).isNotNull();
        assertThat(inbound.getModifiedAt()).isNotNull();
        assertThat(inbound.isDeleted()).isFalse();
        assertThat(inbound.getDeletedAt()).isNull();
    }

    @DisplayName("입고 생성 시 상태를 명시적으로 지정할 수 있다")
    @Test
    void createInboundWithExplicitStatus() {
        // given
        InboundStatus status = InboundStatus.INSPECTING;

        // when
        Inbound inbound = Inbound.builder()
                .warehouseId(1L)
                .supplierId(1L)
                .expectedDate(LocalDate.now())
                .status(status)
                .build();

        // then
        assertThat(inbound.getStatus()).isEqualTo(status);
    }

    @DisplayName("입고 상태 전환이 올바르게 동작한다")
    @ParameterizedTest
    @MethodSource("validStatusTransitions")
    void updateStatusWithValidTransitions(InboundStatus fromStatus, InboundStatus toStatus) {
        // given
        Inbound inbound = createInboundWithStatus(fromStatus);

        // when
        Inbound updatedInbound = inbound.updateStatus(toStatus);

        // then
        assertThat(updatedInbound.getStatus()).isEqualTo(toStatus);
    }

    private static Stream<Arguments> validStatusTransitions() {
        return Stream.of(
                Arguments.of(InboundStatus.REGISTERED, InboundStatus.INSPECTING),
                Arguments.of(InboundStatus.REGISTERED, InboundStatus.CANCELED),
                Arguments.of(InboundStatus.INSPECTING, InboundStatus.COMPLETED),
                Arguments.of(InboundStatus.INSPECTING, InboundStatus.REJECTED)
        );
    }

    @DisplayName("잘못된 입고 상태 전환 시 예외가 발생한다")
    @ParameterizedTest
    @MethodSource("invalidStatusTransitions")
    void updateStatusWithInvalidTransitions(InboundStatus fromStatus, InboundStatus toStatus, String expectedMessage) {
        // given
        Inbound inbound = createInboundWithStatus(fromStatus);

        // when & then
        assertThatThrownBy(() -> inbound.updateStatus(toStatus))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.INVALID_INPUT)
                .hasMessageContaining(expectedMessage);
    }

    private static Stream<Arguments> invalidStatusTransitions() {
        return Stream.of(
                Arguments.of(InboundStatus.REGISTERED, InboundStatus.COMPLETED, "입고 등록 상태에서는 검수 중 혹은 입고 취소 상태로만 변경 가능합니다"),
                Arguments.of(InboundStatus.INSPECTING, InboundStatus.REGISTERED, "검수 중 상태에서는 입고 완료 또는 입고 거절로만 변경 가능합니다"),
                Arguments.of(InboundStatus.COMPLETED, InboundStatus.INSPECTING, "입고 완료 또는 입고 거절 상태에서는 더 이상 상태 변경이 불가능합니다"),
                Arguments.of(InboundStatus.REJECTED, InboundStatus.INSPECTING, "입고 완료 또는 입고 거절 상태에서는 더 이상 상태 변경이 불가능합니다")
        );
    }

    private Inbound createInboundWithStatus(InboundStatus status) {
        return Inbound.builder()
                .warehouseId(1L)
                .supplierId(1L)
                .expectedDate(LocalDate.now())
                .status(status)
                .build();
    }

}
