package inventory.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.product.controller.request.CreateProductRequest;
import inventory.product.controller.request.UpdateProductRequest;
import inventory.product.domain.Product;
import inventory.product.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @DisplayName("상품 생성을 성공하면 저장된 상품 정보를 반환한다")
    @Test
    void saveWithSuccess() {
        // given
        CreateProductRequest request = new CreateProductRequest(
                1L,
                "테스트 상품",
                "PROD001",
                "개",
                "https://example.com/thumbnail.jpg"
        );

        Product savedProduct = Product.builder()
                .productId(1L)
                .supplierId(1L)
                .productName("테스트 상품")
                .productCode("PROD001")
                .unit("개")
                .thumbnailUrl("https://example.com/thumbnail.jpg")
                .active(true)
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // when
        Product result = productService.save(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(1L);
        assertThat(result.getSupplierId()).isEqualTo(1L);
        assertThat(result.getProductName()).isEqualTo("테스트 상품");
        assertThat(result.getProductCode()).isEqualTo("PROD001");
        assertThat(result.getUnit()).isEqualTo("개");
        assertThat(result.getThumbnailUrl()).isEqualTo("https://example.com/thumbnail.jpg");
        assertThat(result.isActive()).isTrue();

        verify(productRepository).save(any(Product.class));
    }

    @DisplayName("null 썸네일 URL로 상품 생성을 성공하면 저장된 상품 정보를 반환한다")
    @Test
    void saveWithNullThumbnailUrl() {
        // given
        CreateProductRequest request = new CreateProductRequest(
                1L,
                "테스트 상품",
                "PROD001",
                "개",
                null
        );

        Product savedProduct = Product.builder()
                .productId(1L)
                .supplierId(1L)
                .productName("테스트 상품")
                .productCode("PROD001")
                .unit("개")
                .thumbnailUrl(null)
                .active(true)
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // when
        Product result = productService.save(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getThumbnailUrl()).isNull();

        verify(productRepository).save(any(Product.class));
    }

    @DisplayName("빈 썸네일 URL로 상품 생성을 성공하면 저장된 상품 정보를 반환한다")
    @Test
    void saveWithEmptyThumbnailUrl() {
        // given
        CreateProductRequest request = new CreateProductRequest(
                1L,
                "테스트 상품",
                "PROD001",
                "개",
                ""
        );

        Product savedProduct = Product.builder()
                .productId(1L)
                .supplierId(1L)
                .productName("테스트 상품")
                .productCode("PROD001")
                .unit("개")
                .thumbnailUrl("")
                .active(true)
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // when
        Product result = productService.save(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getThumbnailUrl()).isEmpty();

        verify(productRepository).save(any(Product.class));
    }

    @DisplayName("상품 ID로 조회를 성공하면 해당 상품 정보를 반환한다")
    @Test
    void findByIdWithSuccess() {
        // given
        Long productId = 1L;
        Product product = Product.builder()
                .productId(productId)
                .supplierId(1L)
                .productName("테스트 상품")
                .productCode("PROD001")
                .unit("개")
                .thumbnailUrl("https://example.com/thumbnail.jpg")
                .active(true)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        Product result = productService.findById(productId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getProductName()).isEqualTo("테스트 상품");

        verify(productRepository).findById(productId);
    }

    @DisplayName("존재하지 않는 상품 ID로 조회 시 예외가 발생한다")
    @Test
    void findByIdWithNotFound() {
        // given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.findById(productId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);

        verify(productRepository).findById(productId);
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
        List<Product> products = List.of(
                Product.builder().productId(1L).supplierId(1L).productName("상품1").productCode("PROD001")
                        .unit("개").thumbnailUrl("https://example.com/1.jpg").active(true).build(),
                Product.builder().productId(2L).supplierId(2L).productName("상품2").productCode("PROD002")
                        .unit("박스").thumbnailUrl("https://example.com/2.jpg").active(true).build()
        );

        when(productRepository.findAll()).thenReturn(products);

        // when
        List<Product> result = productService.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getProductName()).isEqualTo("상품1");
        assertThat(result.get(1).getProductName()).isEqualTo("상품2");

        verify(productRepository).findAll();
    }

    @DisplayName("상품 정보 수정을 성공하면 수정된 상품 정보를 반환한다")
    @Test
    void updateWithSuccess() {
        // given
        Long productId = 1L;
        UpdateProductRequest updateRequest = new UpdateProductRequest(
                "수정된 상품명",
                "https://example.com/new-thumbnail.jpg",
                false
        );

        Product existingProduct = Product.builder()
                .productId(productId)
                .supplierId(1L)
                .productName("기존 상품명")
                .productCode("PROD001")
                .unit("개")
                .thumbnailUrl("https://example.com/old-thumbnail.jpg")
                .active(true)
                .build();

        Product updatedProduct = Product.builder()
                .productId(productId)
                .supplierId(1L) // 업데이트되지 않음
                .productCode("PROD001") // 업데이트되지 않음
                .unit("개") // 업데이트되지 않음
                .productName("수정된 상품명")
                .thumbnailUrl("https://example.com/new-thumbnail.jpg")
                .active(false)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // when
        Product result = productService.update(productId, updateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getProductName()).isEqualTo("수정된 상품명");
        assertThat(result.getThumbnailUrl()).isEqualTo("https://example.com/new-thumbnail.jpg");
        assertThat(result.isActive()).isFalse();
        assertThat(result.getSupplierId()).isEqualTo(1L); // 업데이트되지 않음
        assertThat(result.getProductCode()).isEqualTo("PROD001"); // 업데이트되지 않음
        assertThat(result.getUnit()).isEqualTo("개"); // 업데이트되지 않음

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
    }

    @DisplayName("부분 수정을 성공하면 기존 값과 수정된 값이 함께 반환된다")
    @Test
    void updateWithPartialSuccess() {
        // given
        Long productId = 1L;
        UpdateProductRequest updateRequest = new UpdateProductRequest(
                null, // productName은 수정하지 않음
                null, // thumbnailUrl은 수정하지 않음
                false // active만 수정
        );

        Product existingProduct = Product.builder()
                .productId(productId)
                .supplierId(1L)
                .productName("기존 상품명")
                .productCode("PROD001")
                .unit("개")
                .thumbnailUrl("https://example.com/thumbnail.jpg")
                .active(true)
                .build();

        Product updatedProduct = Product.builder()
                .productId(productId)
                .supplierId(1L) // 업데이트되지 않음
                .productCode("PROD001") // 업데이트되지 않음
                .unit("개") // 업데이트되지 않음
                .productName("기존 상품명") // 기존 값 유지
                .thumbnailUrl("https://example.com/thumbnail.jpg") // 기존 값 유지
                .active(false) // 수정된 값
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // when
        Product result = productService.update(productId, updateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProductName()).isEqualTo("기존 상품명"); // 기존 값 유지
        assertThat(result.getThumbnailUrl()).isEqualTo("https://example.com/thumbnail.jpg"); // 기존 값 유지
        assertThat(result.isActive()).isFalse(); // 수정된 값

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
    }

    @DisplayName("존재하지 않는 상품 수정 시 예외가 발생한다")
    @Test
    void updateWithNotFound() {
        // given
        Long productId = 999L;
        UpdateProductRequest updateRequest = new UpdateProductRequest(
                "수정된 상품명",
                "https://example.com/new-thumbnail.jpg",
                false
        );

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.update(productId, updateRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);

        verify(productRepository).findById(productId);
    }

    @DisplayName("null ID로 상품 수정 시 예외가 발생한다")
    @Test
    void updateWithNullId() {
        // given
        UpdateProductRequest updateRequest = new UpdateProductRequest(
                "수정된 상품명",
                "https://example.com/new-thumbnail.jpg",
                false
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
        Long productId = 1L;
        Product product = Product.builder()
                .productId(productId)
                .supplierId(1L)
                .productName("테스트 상품")
                .productCode("PROD001")
                .unit("개")
                .thumbnailUrl("https://example.com/thumbnail.jpg")
                .active(true)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        productService.deleteById(productId);

        // then
        verify(productRepository).findById(productId);
        verify(productRepository).deleteById(productId);
    }

    @DisplayName("존재하지 않는 상품 삭제 시 예외가 발생한다")
    @Test
    void deleteByIdWithNotFound() {
        // given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.deleteById(productId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);

        verify(productRepository).findById(productId);
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
