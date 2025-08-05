package io.inventory.warehouse.domain;

import io.inventory.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@SQLRestriction("is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "warehouses")
@Entity
public class Warehouse extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long warehouseId;

    private String name;

    private Integer postcode;

    private String baseAddress;

    private String detailAddress;

    private String contact;
}
