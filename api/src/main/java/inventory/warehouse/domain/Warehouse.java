package inventory.warehouse.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted = false and deleted_at is null")
@Getter
@Entity
public class Warehouse {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long warehouseId;

    private String name;

    private String postcode;

    private String baseAddress;

    private String detailAddress;

    private String managerName;

    private String managerContact;

    private boolean active = true;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private boolean deleted = false;

    private LocalDateTime deletedAt;

    @Builder
    public Warehouse(String name, String postcode, String baseAddress, String detailAddress,
                     String managerName,
                     String managerContact) {
        this.name = name;
        this.postcode = postcode;
        this.baseAddress = baseAddress;
        this.detailAddress = detailAddress;
        this.managerName = managerName;
        this.managerContact = managerContact;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public Warehouse update(String name, String postcode, String baseAddress, String detailAddress, String managerName, String managerContact) {
        this.name = name;
        this.postcode = postcode;
        this.baseAddress = baseAddress;
        this.detailAddress = detailAddress;
        this.managerName = managerName;
        this.managerContact = managerContact;
        this.modifiedAt = LocalDateTime.now();
        return this;
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
        Warehouse warehouse = (Warehouse) o;
        return Objects.equals(warehouseId, warehouse.warehouseId) && Objects.equals(name, warehouse.name);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(warehouseId);
        result = 31 * result + Objects.hashCode(name);
        return result;
    }
}
