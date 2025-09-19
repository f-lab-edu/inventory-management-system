package inventory.warehouse.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "warehouse_stock",
        uniqueConstraints = @UniqueConstraint(columnNames = {"warehouse_id", "product_id"}))
@Getter
@Entity
public class WarehouseStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long warehouseStockId;

    private Long warehouseId;

    private Long productId;

    private int quantity;

    private int safetyStock;

    private int reservedQuantity;

    private LocalDateTime modifiedAt;

    @Builder
    public WarehouseStock(Long warehouseId, Long productId, int quantity, int safetyStock) {
        this.warehouseId = warehouseId;
        this.productId = productId;
        this.quantity = quantity;
        this.safetyStock = safetyStock;
        this.modifiedAt = LocalDateTime.now();
    }

    public void increaseStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("재고 증가량은 0보다 커야 합니다.");
        }
        this.quantity += amount;
        this.modifiedAt = LocalDateTime.now();
    }

    public void updateSafetyStock(int safetyStock) {
        if (safetyStock < 0) {
            throw new IllegalArgumentException("안전재고는 0 이상이어야 합니다.");
        }
        this.safetyStock = safetyStock;
        this.modifiedAt = LocalDateTime.now();
    }

    public boolean isBelowSafetyStock() {
        return getAvailableQuantity() < this.safetyStock;
    }

    public void decreaseStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("재고 차감량은 0보다 커야 합니다.");
        }
        if (this.quantity < amount) {
            throw new IllegalArgumentException("재고가 부족합니다. 현재 재고: " + this.quantity + ", 요청 수량: " + amount);
        }
        this.quantity -= amount;
        this.modifiedAt = LocalDateTime.now();
    }

    public boolean hasEnoughStock(int requestedQuantity) {
        return getAvailableQuantity() >= requestedQuantity;
    }

    public int getAvailableQuantity() {
        return this.quantity - this.reservedQuantity;
    }

    public boolean hasEnoughAvailableStock(int requestedQuantity) {
        return getAvailableQuantity() >= requestedQuantity;
    }

    public void reserve(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("예약 수량은 0보다 커야 합니다.");
        }
        if (getAvailableQuantity() < amount) {
            throw new IllegalArgumentException("예약 가능한 재고가 부족합니다. 가용 재고: " + getAvailableQuantity() + ", 요청 수량: " + amount);
        }
        this.reservedQuantity += amount;
        this.modifiedAt = LocalDateTime.now();
    }

    public void releaseReservation(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("해제 수량은 0보다 커야 합니다.");
        }
        if (this.reservedQuantity < amount) {
            throw new IllegalArgumentException("해제할 예약 재고가 부족합니다. 현재 예약: " + this.reservedQuantity + ", 요청 해제: " + amount);
        }
        this.reservedQuantity -= amount;
        this.modifiedAt = LocalDateTime.now();
    }

    public void confirmShipment(int amount) {
        // 출고 확정: 예약을 줄이고 실제 재고를 차감
        if (amount <= 0) {
            throw new IllegalArgumentException("차감 수량은 0보다 커야 합니다.");
        }
        if (this.reservedQuantity < amount) {
            throw new IllegalArgumentException("예약 재고가 부족합니다. 현재 예약: " + this.reservedQuantity + ", 요청: " + amount);
        }
        if (this.quantity < amount) {
            throw new IllegalArgumentException("실재 재고가 부족합니다. 현재 재고: " + this.quantity + ", 요청: " + amount);
        }
        this.reservedQuantity -= amount;
        this.quantity -= amount;
        this.modifiedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WarehouseStock that = (WarehouseStock) o;
        return Objects.equals(warehouseStockId, that.warehouseStockId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(warehouseStockId);
    }
}
