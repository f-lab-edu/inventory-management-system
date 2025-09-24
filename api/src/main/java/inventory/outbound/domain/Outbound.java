package inventory.outbound.domain;

import inventory.outbound.domain.enums.OutboundStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Outbound {

    private static final LocalTime OUTBOUND_CUTOFF_TIME = LocalTime.of(10, 0); // 10시 컷오프 시간

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long outboundId;

    private Long warehouseId;

    private String orderNumber;

    private String recipientName;

    private String recipientContact;

    private String deliveryPostcode;

    private String deliveryBaseAddress;

    private String deliveryDetailAddress;

    private LocalDate requestedDate;

    private LocalDate expectedDate;

    private LocalDate shippedDate;

    private String deliveryMemo;

    @Enumerated(EnumType.STRING)
    private OutboundStatus outboundStatus;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @Builder
    public Outbound(
            Long warehouseId, String orderNumber, String recipientName, String recipientContact,
            String deliveryPostcode, String deliveryBaseAddress, String deliveryDetailAddress,
            LocalDate requestedDate, String deliveryMemo, OutboundStatus outboundStatus
    ) {
        this.warehouseId = warehouseId;
        this.orderNumber = orderNumber != null ? orderNumber : generateOrderNumber();
        this.recipientName = recipientName;
        this.recipientContact = recipientContact;
        this.deliveryPostcode = deliveryPostcode;
        this.deliveryBaseAddress = deliveryBaseAddress;
        this.deliveryDetailAddress = deliveryDetailAddress;
        this.requestedDate = requestedDate;
        this.expectedDate = calculateExpectedDate();
        this.shippedDate = null;
        this.deliveryMemo = deliveryMemo;
        this.outboundStatus = outboundStatus != null ? outboundStatus : OutboundStatus.ORDERED;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    private LocalDate calculateExpectedDate() {
        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        if (Objects.equals(this.requestedDate, today)) {
            if (currentTime.isBefore(OUTBOUND_CUTOFF_TIME)) {
                return today;
            }
            return today.plusDays(1);
        }

        return this.requestedDate;
    }

    public Outbound updateStatus(OutboundStatus newStatus) {
        validateStatusTransition(this.outboundStatus, newStatus);
        this.outboundStatus = newStatus;
        this.modifiedAt = LocalDateTime.now();

        if (newStatus == OutboundStatus.SHIPPED) {
            this.shippedDate = LocalDate.now();
        }

        return this;
    }

    private void validateStatusTransition(OutboundStatus currentStatus, OutboundStatus newStatus) {
        switch (currentStatus) {
            case ORDERED:
                if (newStatus != OutboundStatus.PICKING && newStatus != OutboundStatus.CANCELED) {
                    throw new IllegalArgumentException("출고 등록 상태에서는 피킹 중 또는 취소로만 변경 가능합니다.");
                }
                break;
            case PICKING:
                if (newStatus != OutboundStatus.SHIPPED && newStatus != OutboundStatus.CANCELED) {
                    throw new IllegalArgumentException("피킹 중 상태에서는 출고 완료 또는 취소로만 변경 가능합니다.");
                }
                break;
            case SHIPPED:
                throw new IllegalArgumentException("출고 완료 상태에서는 더 이상 상태 변경이 불가능합니다.");
            default:
                throw new IllegalArgumentException("알 수 없는 상태입니다.");
        }
    }

    public boolean canBeCanceled() {
        return this.outboundStatus == OutboundStatus.ORDERED || this.outboundStatus == OutboundStatus.PICKING;
    }

    private String generateOrderNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "OB" + datePart + "-" + randomPart;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Outbound outbound = (Outbound) o;
        return Objects.equals(outboundId, outbound.outboundId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outboundId);
    }
}