package inventory.supplier.domain;

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
public class Supplier {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
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
    public Supplier(String name, String businessRegistrationNumber, String postcode,
                    String baseAddress, String detailAddress, String ceoName, String managerName,
                    String managerContact) {
        this.name = name;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.postcode = postcode;
        this.baseAddress = baseAddress;
        this.detailAddress = detailAddress;
        this.ceoName = ceoName;
        this.managerName = managerName;
        this.managerContact = managerContact;
    }

    public Supplier update(String postcode, String baseAddress, String detailAddress, String ceoName, String managerName, String managerContact) {
        this.postcode = postcode;
        this.baseAddress = baseAddress;
        this.detailAddress = detailAddress;
        this.ceoName = ceoName;
        this.managerName = managerName;
        this.managerContact = managerContact;
        return this;
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
