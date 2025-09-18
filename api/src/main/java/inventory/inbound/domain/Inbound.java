package inventory.inbound.domain;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.inbound.domain.enums.InboundStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Inbound {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long inboundId;

    private Long warehouseId;

    private Long supplierId;

    private LocalDate expectedDate;

    @Enumerated(EnumType.STRING)
    private InboundStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private boolean deleted = false;

    private LocalDateTime deletedAt;

    @Builder
    public Inbound(Long warehouseId, Long supplierId, LocalDate expectedDate, InboundStatus status) {
        this.warehouseId = warehouseId;
        this.supplierId = supplierId;
        this.expectedDate = expectedDate;
        this.status = status != null ? status : InboundStatus.REGISTERED;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public Inbound updateStatus(InboundStatus newStatus) {
        validateStatusTransition(this.status, newStatus);
        this.status = newStatus;
        this.modifiedAt = LocalDateTime.now();
        return this;
    }

    public void validateStatusTransition(InboundStatus currentStatus, InboundStatus newStatus) {
        switch (currentStatus) {
            case REGISTERED:
                if (newStatus != InboundStatus.INSPECTING && newStatus != InboundStatus.CANCELED) {
                    throw new CustomException(ExceptionCode.INVALID_INPUT,
                            "입고 등록 상태에서는 검수 중 혹은 입고 취소 상태로만 변경 가능합니다.");
                }
                break;
            case INSPECTING:
                if (newStatus != InboundStatus.COMPLETED && newStatus != InboundStatus.REJECTED) {
                    throw new CustomException(ExceptionCode.INVALID_INPUT,
                            "검수 중 상태에서는 입고 완료 또는 입고 거절로만 변경 가능합니다.");
                }
                break;
            case COMPLETED:
            case REJECTED:
                throw new CustomException(ExceptionCode.INVALID_INPUT,
                        "입고 완료 또는 입고 거절 상태에서는 더 이상 상태 변경이 불가능합니다.");
            default:
                throw new CustomException(ExceptionCode.INVALID_INPUT,
                        "알 수 없는 상태입니다.");
        }
    }

    public void softDelete() {
        deleted = true;
        deletedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Inbound inbound = (Inbound) o;
        return Objects.equals(inboundId, inbound.inboundId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inboundId);
    }
}