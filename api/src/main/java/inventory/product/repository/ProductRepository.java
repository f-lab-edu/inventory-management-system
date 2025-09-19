package inventory.product.repository;

import inventory.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductQueryRepository {

    @Query("SELECT p FROM Product p WHERE p.productId IN :productIds")
    List<Product> findByIds(@Param("productIds") List<Long> productIds);

}
