package inventory.product.service;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.product.controller.request.CreateProductRequest;
import inventory.product.controller.request.UpdateProductRequest;
import inventory.product.domain.Product;
import inventory.product.repository.ProductRepository;
import inventory.supplier.domain.Supplier;
import inventory.supplier.repository.SupplierRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    private Supplier createTestSupplier(String name, String businessRegistrationNumber) {
        Supplier supplier = Supplier.builder()
                .name(name)
                .businessRegistrationNumber(businessRegistrationNumber)
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("상세주소")
                .ceoName("대표")
                .managerName("매니저")
                .managerContact("01012345678")
                .build();
        return supplierRepository.save(supplier);
    }

    @DisplayName("상품 생성을 성공하면 저장된 상품 정보를 반환한다")
    @Test
    void saveWithSuccess() {
        // given
        Supplier testSupplier = createTestSupplier("테스트 공급업체", "1234567890");
        
        CreateProductRequest request = new CreateProductRequest(
                testSupplier.getSupplierId(),
                "테스트 상품",
                "PROD001",
                "개",
                "https://example.com/thumbnail.jpg"
        );

        // when
        Product result = productService.save(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isNotNull();
        assertThat(result.getSupplierId()).isEqualTo(testSupplier.getSupplierId());
        assertThat(result.getProductName()).isEqualTo("테스트 상품");
        assertThat(result.getProductCode()).isEqualTo("PROD001");
        assertThat(result.getUnit()).isEqualTo("개");
        assertThat(result.getThumbnailUrl()).isEqualTo("https://example.com/thumbnail.jpg");
    }

    @DisplayName("null 썸네일 URL로 상품 생성을 성공하면 저장된 상품 정보를 반환한다")
    @Test
    void saveWithNullThumbnailUrl() {
        // given
        Supplier testSupplier = createTestSupplier("테스트 공급업체2", "1234567891");
        
        CreateProductRequest request = new CreateProductRequest(
                testSupplier.getSupplierId(),
                "테스트 상품",
                "PROD002",
                "개",
                null
        );

        // when
        Product result = productService.save(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getThumbnailUrl()).isEqualTo("thumbnail/default.png"); // 기본값으로 설정됨
    }

    @DisplayName("빈 썸네일 URL로 상품 생성을 성공하면 저장된 상품 정보를 반환한다")
    @Test
    void saveWithEmptyThumbnailUrl() {
        // given
        Supplier testSupplier = createTestSupplier("빈 URL 테스트 공급업체", "1234567895");
        
        CreateProductRequest request = new CreateProductRequest(
                testSupplier.getSupplierId(),
                "테스트 상품",
                "PROD006",
                "개",
                ""
        );

        // when
        Product result = productService.save(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getThumbnailUrl()).isEmpty();
    }

    @DisplayName("상품 ID로 조회를 성공하면 해당 상품 정보를 반환한다")
    @Test
    void findByIdWithSuccess() {
        // given
        Supplier testSupplier = createTestSupplier("조회 테스트 공급업체", "1234567892");

        CreateProductRequest request = new CreateProductRequest(
                testSupplier.getSupplierId(),
                "조회 테스트 상품",
                "PROD003",
                "개",
                "https://example.com/thumbnail.jpg"
        );
        Product savedProduct = productService.save(request);
        Long productId = savedProduct.getProductId();

        // when
        Product result = productService.findById(productId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getProductName()).isEqualTo("조회 테스트 상품");
        assertThat(result.getSupplierId()).isEqualTo(testSupplier.getSupplierId());
    }

    @DisplayName("존재하지 않는 상품 ID로 조회 시 예외가 발생한다")
    @Test
    void findByIdWithNotFound() {
        // given
        Long productId = 999L;

        // when & then
        assertThatThrownBy(() -> productService.findById(productId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);
    }

    @DisplayName("null ID로 상품 조회 시 예외가 발생한다")
    @Test
    void findByIdWithNullId() {
        // when & then
        assertThatThrownBy(() -> productService.findById(null))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.INVALID_INPUT);
    }

    @DisplayName("전체 상품 조회를 성공하면 상품 목록을 반환한다")
    @Test
    void findAllWithSuccess() {
        // given
        Supplier supplier1 = createTestSupplier("공급업체1", "1234567896");
        Supplier supplier2 = createTestSupplier("공급업체2", "1234567897");

        CreateProductRequest request1 = new CreateProductRequest(
                supplier1.getSupplierId(),
                "상품1",
                "PROD007",
                "개",
                "https://example.com/1.jpg"
        );

        CreateProductRequest request2 = new CreateProductRequest(
                supplier2.getSupplierId(),
                "상품2",
                "PROD008",
                "박스",
                "https://example.com/2.jpg"
        );

        productService.save(request1);
        productService.save(request2);

        // when
        List<Product> result = productService.findAll();

        // then
        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
        assertThat(result.stream().anyMatch(p -> p.getProductName().equals("상품1"))).isTrue();
        assertThat(result.stream().anyMatch(p -> p.getProductName().equals("상품2"))).isTrue();
    }

    @DisplayName("상품 정보 수정을 성공하면 수정된 상품 정보를 반환한다")
    @Test
    void updateWithSuccess() {
        // given
        Supplier testSupplier = createTestSupplier("수정 테스트 공급업체", "1234567893");

        CreateProductRequest createRequest = new CreateProductRequest(
                testSupplier.getSupplierId(),
                "기존 상품명",
                "PROD004",
                "개",
                "https://example.com/old-thumbnail.jpg"
        );
        Product savedProduct = productService.save(createRequest);
        Long productId = savedProduct.getProductId();

        UpdateProductRequest updateRequest = new UpdateProductRequest(
                "수정된 상품명",
                "https://example.com/new-thumbnail.jpg"
        );

        // when
        Product result = productService.update(productId, updateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getProductName()).isEqualTo("수정된 상품명");
        assertThat(result.getThumbnailUrl()).isEqualTo("https://example.com/new-thumbnail.jpg");
        assertThat(result.isActive()).isFalse();
        assertThat(result.getSupplierId()).isEqualTo(testSupplier.getSupplierId()); // 업데이트되지 않음
        assertThat(result.getProductCode()).isEqualTo("PROD004"); // 업데이트되지 않음
        assertThat(result.getUnit()).isEqualTo("개"); // 업데이트되지 않음

        Product updatedProduct = productRepository.findById(productId).orElse(null);
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getProductName()).isEqualTo("수정된 상품명");
        assertThat(updatedProduct.isActive()).isFalse();
    }

    @DisplayName("부분 수정을 성공하면 기존 값과 수정된 값이 함께 반환된다")
    @Test
    void updateWithPartialSuccess() {
        // given
        Supplier testSupplier = createTestSupplier("부분 수정 테스트 공급업체", "1234567898");

        CreateProductRequest createRequest = new CreateProductRequest(
                testSupplier.getSupplierId(),
                "기존 상품명",
                "PROD009",
                "개",
                "https://example.com/thumbnail.jpg"
        );
        Product savedProduct = productService.save(createRequest);
        Long productId = savedProduct.getProductId();

        UpdateProductRequest updateRequest = new UpdateProductRequest(
                "수정 상품명",
                "https://example.com/thumbnail.jpg"
        );

        // when
        productService.update(productId, updateRequest);

        // then
        Product updatedProduct = productRepository.findById(productId).orElse(null);
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getProductName()).isEqualTo("수정 상품명");
    }

    @DisplayName("존재하지 않는 상품 수정 시 예외가 발생한다")
    @Test
    void updateWithNotFound() {
        // given
        Long productId = 999L;
        UpdateProductRequest updateRequest = new UpdateProductRequest(
                "수정된 상품명",
                "https://example.com/new-thumbnail.jpg"
        );

        // when & then
        assertThatThrownBy(() -> productService.update(productId, updateRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);
    }

    @DisplayName("null ID로 상품 수정 시 예외가 발생한다")
    @Test
    void updateWithNullId() {
        // given
        UpdateProductRequest updateRequest = new UpdateProductRequest(
                "수정된 상품명",
                "https://example.com/new-thumbnail.jpg"
        );

        // when & then
        assertThatThrownBy(() -> productService.update(null, updateRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.INVALID_INPUT);
    }

    @DisplayName("상품 삭제를 성공하면 상품이 삭제된다")
    @Test
    void deleteByIdWithSuccess() {
        // given
        Supplier testSupplier = createTestSupplier("삭제 테스트 공급업체", "1234567894");

        CreateProductRequest request = new CreateProductRequest(
                testSupplier.getSupplierId(),
                "삭제할 상품",
                "PROD005",
                "개",
                "https://example.com/thumbnail.jpg"
        );
        Product savedProduct = productService.save(request);
        Long productId = savedProduct.getProductId();

        // when
        productService.deleteById(productId);

        // then
        assertThat(productRepository.findById(productId)).isEmpty();
    }

    @DisplayName("존재하지 않는 상품 삭제 시 예외가 발생한다")
    @Test
    void deleteByIdWithNotFound() {
        // given
        Long productId = 999L;

        // when & then
        assertThatThrownBy(() -> productService.deleteById(productId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);
    }

    @DisplayName("null ID로 상품 삭제 시 예외가 발생한다")
    @Test
    void deleteByIdWithNullId() {
        // when & then
        assertThatThrownBy(() -> productService.deleteById(null))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.INVALID_INPUT);
    }
}
