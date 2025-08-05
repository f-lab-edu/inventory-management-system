package io.inventory.stock.domain;

import io.inventory.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@SQLRestriction("is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "stocks")
@Entity
public class Stock extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long stockId;

    private Long productId;

    private Long warehouseId;

    private String locationInWarehouse;

    private Integer actualStock;

    private Integer allocatedStock;

    private Integer safetyUnitStock;

    @Enumerated(EnumType.STRING)
    private StockStatus stockStatus;

    public void increaseStock(int quantity) {
        validatePositive(quantity);
        this.actualStock += quantity;
        updateStatus();
    }

    public void decreaseStock(int quantity) {
        validatePositive(quantity);
        if (this.actualStock - quantity < 0) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.actualStock -= quantity;
        updateStatus();
    }

    private void updateStatus() {
        if (this.actualStock <= 0) {
            this.stockStatus = StockStatus.OUT_OF_STOCK;
            return;
        }

        if (this.actualStock < this.safetyUnitStock) {
            this.stockStatus = StockStatus.LOW_STOCK;
            return;
        }

        this.stockStatus = StockStatus.IN_STOCK;
    }

    private void validatePositive(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
        }
    }
}
