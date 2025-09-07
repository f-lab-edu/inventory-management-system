package inventory.warehouse.domain;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Warehouse {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    private Long warehouseId;

    private String name;

    private String postcode;

    private String baseAddress;

    private String detailAddress;

    private String managerName;

    private String managerContact;

    private boolean active = true;

    @Builder
    public Warehouse(Long warehouseId, String name, String postcode, String baseAddress, String detailAddress,
                     String managerName,
                     String managerContact) {
        this.warehouseId = warehouseId != null ? warehouseId : ID_GENERATOR.getAndIncrement();
        this.name = name;
        this.postcode = postcode;
        this.baseAddress = baseAddress;
        this.detailAddress = detailAddress;
        this.managerName = managerName;
        this.managerContact = managerContact;
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
