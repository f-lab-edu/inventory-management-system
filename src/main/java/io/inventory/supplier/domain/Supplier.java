package io.inventory.supplier.domain;

import io.inventory.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@SQLRestriction("is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "suppliers")
@Entity
public class Supplier extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long supplierId;

    private String name;

    private String contact;

    private Integer postcode;

    private String baseAddress;

    private String detailAddress;

    public void updateContactInfo(String contact, Integer postcode, String baseAddress, String detailAddress) {
        this.contact = contact;
        this.postcode = postcode;
        this.baseAddress = baseAddress;
        this.detailAddress = detailAddress;
    }
}
