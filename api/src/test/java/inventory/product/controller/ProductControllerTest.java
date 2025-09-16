package inventory.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import inventory.common.exception.GlobalExceptionHandler;
import inventory.product.controller.request.CreateProductRequest;
import inventory.product.controller.request.UpdateProductRequest;
import inventory.product.controller.response.ProductResponse;
import inventory.product.domain.Product;
import inventory.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    private static final String BASE_URL = "/api/v1/products";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @DisplayName("상품 생성을 성공하면 CREATED 상태와 상품 정보를 반환한다")
    @Test
    void createProductWithSuccess() throws Exception {
        // given
        CreateProductRequest request = new CreateProductRequest(
                1L, "테스트 상품", "PROD001", "EA", "https://example.com/thumbnail.jpg"
        );

        Product savedProduct = Product.builder()
                .supplierId(1L)
                .productName("테스트 상품")
                .productCode("PROD001")
                .unit("EA")
                .thumbnailUrl("https://example.com/thumbnail.jpg")
                .active(true)
                .build();

        when(productService.save(any(CreateProductRequest.class)))
                .thenReturn(ProductResponse.from(savedProduct));

        // when & then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.name()))
                .andExpect(jsonPath("$.data.productName").value("테스트 상품"))
                .andExpect(jsonPath("$.data.supplierId").value(1L))
                .andExpect(jsonPath("$.data.productCode").value("PROD001"))
                .andExpect(jsonPath("$.data.thumbnailUrl").value("https://example.com/thumbnail.jpg"))
                .andExpect(jsonPath("$.data.unit").value("EA"))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @DisplayName("상품 조회를 성공하면 OK 상태와 상품 정보를 반환한다")
    @Test
    void getProductWithSuccess() throws Exception {
        // given
        Long productId = 1L;
        Product product = Product.builder()
                .supplierId(1L)
                .productName("테스트 상품")
                .productCode("PROD001")
                .unit("EA")
                .thumbnailUrl("https://example.com/thumbnail.jpg")
                .active(true)
                .build();

        when(productService.findById(productId)).thenReturn(ProductResponse.from(product));

        // when & then
        mockMvc.perform(get(BASE_URL + "/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productName").value("테스트 상품"))
                .andExpect(jsonPath("$.data.supplierId").value(1L))
                .andExpect(jsonPath("$.data.productCode").value("PROD001"))
                .andExpect(jsonPath("$.data.unit").value("EA"))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @DisplayName("상품 목록 조회를 성공하면 페이징된 결과를 반환한다")
    @Test
    void searchProductWithSuccess() throws Exception {
        // given
        Product product = Product.builder()
                .supplierId(1L)
                .productName("테스트 상품")
                .productCode("PROD001")
                .unit("EA")
                .thumbnailUrl("https://example.com/thumbnail.jpg")
                .active(true)
                .build();

        when(productService.findAll()).thenReturn(List.of(product));

        // when & then
        mockMvc.perform(get(BASE_URL)
                        .param("currentPageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.currentPageNumber").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @DisplayName("상품 정보 수정을 성공하면 수정된 정보를 반환한다")
    @Test
    void updateProductWithSuccess() throws Exception {
        // given
        Long productId = 1L;
        UpdateProductRequest request = new UpdateProductRequest(
                "수정된 상품명", "https://example.com/new-thumbnail.jpg"
        );

        Product updatedProduct = Product.builder()
                .supplierId(1L)
                .productName("수정된 상품명")
                .productCode("PROD001")
                .unit("EA")
                .thumbnailUrl("https://example.com/new-thumbnail.jpg")
                .active(false)
                .build();

        when(productService.update(eq(productId), any(UpdateProductRequest.class)))
                .thenReturn(ProductResponse.from(updatedProduct));

        // when & then
        mockMvc.perform(put(BASE_URL + "/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productName").value("수정된 상품명"))
                .andExpect(jsonPath("$.data.thumbnailUrl").value("https://example.com/new-thumbnail.jpg"))
                .andExpect(jsonPath("$.data.active").value(false));
    }

    @DisplayName("상품 삭제를 성공하면 NO_CONTENT 상태를 반환한다")
    @Test
    void deleteProductWithSuccess() throws Exception {
        // given
        Long productId = 1L;

        // when & then
        mockMvc.perform(delete(BASE_URL + "/" + productId))
                .andExpect(status().isNoContent());
    }

    @DisplayName("썸네일이 null인 상품 생성 시 DEFAULT 값이 설정된다")
    @Test
    void createProductWithNullThumbnailUrl() throws Exception {
        // given
        CreateProductRequest request = new CreateProductRequest(
                1L, "테스트 상품", "PROD001", "EA", null
        );

        Product savedProduct = Product.builder()
                .supplierId(1L)
                .productName("테스트 상품")
                .productCode("PROD001")
                .unit("EA")
                .thumbnailUrl(null) // null로 설정하면 Product 엔티티에서 기본값 처리
                .active(true)
                .build();

        when(productService.save(any(CreateProductRequest.class))).thenReturn(ProductResponse.from(savedProduct));

        // when & then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.name()))
                .andExpect(jsonPath("$.data.productName").value("테스트 상품"))
                .andExpect(jsonPath("$.data.supplierId").value(1L))
                .andExpect(jsonPath("$.data.productCode").value("PROD001"))
                .andExpect(jsonPath("$.data.thumbnailUrl").value("thumbnail/default.png"))
                .andExpect(jsonPath("$.data.unit").value("EA"))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @DisplayName("유효하지 않은 상품 생성 요청 시 400을 반환한다")
    @Test
    void createProductWithValidationFail() throws Exception {
        // given
        CreateProductRequest invalidRequest = new CreateProductRequest(
                null, "", "", "", "https://example.com/thumbnail.jpg"
        );

        // when & then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}