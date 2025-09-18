package inventory.product.domain;

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
public class Product {

    private static final String THUMBNAIL_DEFAULT_PNG = "thumbnail/default.png";

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long productId;

    private Long supplierId;

    private String productName;

    private String productCode;

    private String unit;

    private String thumbnailUrl;

    private boolean active = true;

    @Builder
    public Product(Long supplierId, String productName, String productCode, String unit,
                   String thumbnailUrl, boolean active) {
        this.supplierId = supplierId;
        this.productName = productName;
        this.productCode = productCode;
        this.unit = unit;
        this.thumbnailUrl = thumbnailUrl != null ? thumbnailUrl : THUMBNAIL_DEFAULT_PNG;
        this.active = active;
    }

    public void deactivate() {
        this.active = false;
    }

    public Product update(Product updateProduct) {
        this.productName = updateProduct.productName;
        this.thumbnailUrl = updateProduct.thumbnailUrl;
        this.active = updateProduct.active;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Product product = (Product) o;
        return Objects.equals(productId, product.productId) && Objects.equals(productCode,
                product.productCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productCode);
    }
}
