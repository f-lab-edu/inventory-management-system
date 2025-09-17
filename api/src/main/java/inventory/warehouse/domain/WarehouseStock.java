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
        return this.quantity < this.safetyStock;
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
