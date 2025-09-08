package inventory.product.domain;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Product {

    private static final AtomicLong ID_GENERATOR = new AtomicLong();
    public static final String THUMBNAIL_DEFAULT_PNG = "thumbnail/default.png";

    private Long productId;

    private Long supplierId;

    private String productName;

    private String productCode;

    private String unit;

    private String thumbnailUrl;

    private boolean active;

    @Builder
    public Product(Long productId, Long supplierId, String productName, String productCode, String unit,
                   String thumbnailUrl, boolean active) {
        this.productId = productId != null ? productId : ID_GENERATOR.getAndIncrement();
        this.supplierId = supplierId;
        this.productName = productName;
        this.productCode = productCode;
        this.unit = unit;
        this.thumbnailUrl = thumbnailUrl != null ? thumbnailUrl : THUMBNAIL_DEFAULT_PNG;
        this.active = active;
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
