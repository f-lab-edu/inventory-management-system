package inventory.warehouse.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    }

    public void deactivate() {
        this.active = false;
    }

    public Warehouse update(Warehouse updateWarehouse) {
        this.name = updateWarehouse.name;
        this.postcode = updateWarehouse.postcode;
        this.baseAddress = updateWarehouse.baseAddress;
        this.detailAddress = updateWarehouse.detailAddress;
        this.managerName = updateWarehouse.managerName;
        this.managerContact = updateWarehouse.managerContact;
        return this;
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
