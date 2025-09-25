package inventory.product.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE product SET deleted = true, deleted_at = NOW() WHERE product_id = ?")
@SQLRestriction("deleted = false and deleted_at is null")
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

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private boolean deleted = false;

    private LocalDateTime deletedAt;

    @Builder
    public Product(Long supplierId, String productName, String productCode, String unit,
                   String thumbnailUrl, Boolean active) {
        this.supplierId = supplierId;
        this.productName = productName;
        this.productCode = productCode;
        this.unit = unit;
        this.thumbnailUrl = thumbnailUrl != null ? thumbnailUrl : THUMBNAIL_DEFAULT_PNG;
        this.active = active != null ? active : true;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public Product update(String productName, String thumbnailUrl) {
        this.productName = productName;
        this.thumbnailUrl = thumbnailUrl;
        this.modifiedAt = LocalDateTime.now();
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
