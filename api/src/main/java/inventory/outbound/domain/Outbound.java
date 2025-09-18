package inventory.outbound.domain;

import inventory.outbound.domain.enums.OutboundStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

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
        this.orderNumber = orderNumber;
        this.recipientName = recipientName;
        this.recipientContact = recipientContact;
        this.deliveryPostcode = deliveryPostcode;
        this.deliveryBaseAddress = deliveryBaseAddress;
        this.deliveryDetailAddress = deliveryDetailAddress;
        this.requestedDate = requestedDate;
        this.expectedDate = calculateExpectedDate();
        this.shippedDate = null;
        this.deliveryMemo = deliveryMemo;
        this.outboundStatus = outboundStatus;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    /**
     * 출고 예정일 설정
     * 1. 출고 주문 생성 시간이 10시 이전이고, requestedDate가 당일이면 expectedDate도 당일로 설정
     * 2. 출고 주문 생성 시간이 10시 이후이고, requestedDate가 당일이면 expectedDate를 익일로 설정
     * 3. requestedDate가 당일이 아닌 경우 requestedDate를 그대로 사용
     *
     * @return 출고 예정일
     */
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