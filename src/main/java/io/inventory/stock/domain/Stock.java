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
}
