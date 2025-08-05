package io.inventory.product.domain;

import io.inventory.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@SQLRestriction("is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "products")
@Entity
public class Product extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long productId;

    private Long supplierId;

    private Long categoryId;

    private String productCode;

    private String name;

    private String unit;

    private Integer unitPrice;

    private String description;

}
