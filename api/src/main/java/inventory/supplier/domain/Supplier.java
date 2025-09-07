package inventory.supplier.domain;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Supplier {

    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    private Long supplierId;

    private String name;

    private String businessRegistrationNumber;

    private String postcode;

    private String baseAddress;

    private String detailAddress;

    private String ceoName;

    private String managerName;

    private String managerContact;

    private boolean active = true;

    @Builder
    public Supplier(Long supplierId, String name, String businessRegistrationNumber, String postcode,
                    String baseAddress, String detailAddress, String ceoName, String managerName,
                    String managerContact) {
        this.supplierId = supplierId != null ? supplierId : ID_GENERATOR.getAndIncrement();
        this.name = name;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.postcode = postcode;
        this.baseAddress = baseAddress;
        this.detailAddress = detailAddress;
        this.ceoName = ceoName;
        this.managerName = managerName;
        this.managerContact = managerContact;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Supplier supplier = (Supplier) o;
        return Objects.equals(supplierId, supplier.supplierId) && Objects.equals(name, supplier.name);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(supplierId);
        result = 31 * result + Objects.hashCode(name);
        return result;
    }
}
