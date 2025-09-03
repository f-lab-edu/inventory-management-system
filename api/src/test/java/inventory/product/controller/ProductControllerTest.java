package inventory.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import inventory.common.exception.GlobalExceptionHandler;
import inventory.product.controller.request.CreateProductRequest;
import inventory.product.controller.request.UpdateProductRequest;
import inventory.supplier.controller.SupplierController;
import inventory.supplier.controller.response.SupplierResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
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

    private Long testSupplierId;
    private String testSupplierName;

    @BeforeEach
    void setUp() {
        ProductController.PRODUCT_STORE.clear();
        ProductController.ID_GENERATOR.set(1);
        SupplierController.SUPPLIER_STORE.clear();

        // 테스트용 공급업체 데이터 직접 생성
        createTestSupplier();
    }

    private void createTestSupplier() {
        testSupplierId = 1L;
        testSupplierName = "테스트 공급업체";
        
        SupplierResponse supplierResponse = SupplierResponse.of(
                testSupplierId,
                testSupplierName,
                "1234567890",
                "12345",
                "서울시 어딘가",
                "상세주소",
                "김수용",
                "김매니저",
                "01012345678",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        
        SupplierController.SUPPLIER_STORE.put(testSupplierId, supplierResponse);
    }

    @DisplayName("제품 생성을 성공하면 OK 상태와 제품 정보를 반환한다.")
    @Test
    void createProductWithSuccess() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
                testSupplierId,
                "테스트 제품",
                "PROD001",
                "EA",
                "https://example.com/thumbnail.jpg"
        );

        ResultActions result = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.name()))
                .andExpect(jsonPath("$.data.productName").value("테스트 제품"))
                .andExpect(jsonPath("$.data.supplierId").value(testSupplierId))
                .andExpect(jsonPath("$.data.supplierName").value(testSupplierName))
                .andExpect(jsonPath("$.data.productCode").value("PROD001"))
                .andExpect(jsonPath("$.data.thumbnailUrl").value("https://example.com/thumbnail.jpg"))
                .andExpect(jsonPath("$.data.unit").value("EA"))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @DisplayName("존재하지 않는 공급업체 ID로 제품 생성 시 404를 반환한다.")
    @Test
    void createProductWithNonExistentSupplier() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
                999L,
                "테스트 제품",
                "PROD001",
                "EA",
                "https://example.com/thumbnail.jpg"
        );

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @DisplayName("제품 페이징 조회를 성공하면 createdAt 역순으로 정렬하여 반환한다")
    @Test
    void searchProductWithSuccess() throws Exception {
        // 여러 제품 생성
        for (int i = 0; i < 10; i++) {
            CreateProductRequest request = new CreateProductRequest(
                    testSupplierId,
                    "테스트 제품" + i,
                    "PROD00" + i,
                    "EA",
                    "https://example.com/thumbnail" + i + ".jpg"
            );

            mockMvc.perform(post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        ResultActions result = mockMvc.perform(get(BASE_URL)
                .param("currentPageNumber", "0")
                .param("pageSize", "5")
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(5))
                .andExpect(jsonPath("$.data.currentPageNumber").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(5))
                .andExpect(jsonPath("$.data.totalElements").value(10))
                .andExpect(jsonPath("$.data.hasNext").value(true));

        // createdAt 역순(내림차순) 검증
        String body = mockMvc.perform(get(BASE_URL)
                        .param("currentPageNumber", "0")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        String firstCreatedAt = JsonPath.read(body, "$.data.content[0].createdAt");
        String secondCreatedAt = JsonPath.read(body, "$.data.content[1].createdAt");
        LocalDateTime first = LocalDateTime.parse(firstCreatedAt);
        LocalDateTime second = LocalDateTime.parse(secondCreatedAt);
        assertThat(first).isAfterOrEqualTo(second);
    }

    @DisplayName("단일 제품 조회를 성공하면 해당 제품 정보를 반환한다")
    @Test
    void getProductWithSuccess() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
                testSupplierId,
                "테스트 제품",
                "PROD001",
                "EA",
                "https://example.com/thumbnail.jpg"
        );

        String body = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        String id = JsonPath.read(body, "$.data.productId").toString();

        mockMvc.perform(get(BASE_URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productId").value(Integer.parseInt(id)))
                .andExpect(jsonPath("$.data.productName").value("테스트 제품"))
                .andExpect(jsonPath("$.data.supplierId").value(testSupplierId));
    }

    @DisplayName("존재하지 않는 제품을 조회하면 404를 반환한다")
    @Test
    void getProductWithNotFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/9999"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("제품 정보 수정을 성공하면 수정된 정보를 반환한다")
    @Test
    void updateProductWithSuccess() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
                testSupplierId,
                "테스트 제품",
                "PROD001",
                "EA",
                "https://example.com/thumbnail.jpg"
        );

        String body = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        String id = JsonPath.read(body, "$.data.productId").toString();

        UpdateProductRequest update = new UpdateProductRequest(
                "수정된 제품명",
                "https://example.com/new-thumbnail.jpg",
                false
        );

        mockMvc.perform(put(BASE_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productName").value("수정된 제품명"))
                .andExpect(jsonPath("$.data.thumbnailUrl").value("https://example.com/new-thumbnail.jpg"))
                .andExpect(jsonPath("$.data.active").value(false));
    }

    @DisplayName("제품 삭제를 성공하면 204를 반환한다")
    @Test
    void deleteProductWithSuccess() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
                testSupplierId,
                "삭제할 제품",
                "PROD001",
                "EA",
                "https://example.com/thumbnail.jpg"
        );

        String body = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        String id = JsonPath.read(body, "$.data.productId").toString();

        mockMvc.perform(delete(BASE_URL + "/" + id))
                .andExpect(status().isNoContent());
    }

    @DisplayName("thumbnailUrl이 null인 경우 DEFAULT 값이 설정된다")
    @Test
    void createProductWithNullThumbnailUrl() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
                testSupplierId,
                "테스트 제품",
                "PROD001",
                "EA",
                null
        );

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.thumbnailUrl").value("DEFAULT"));
    }

    @DisplayName("유효하지 않은 제품 생성 요청을 하면 400을 반환한다")
    @Test
    void createProductWithValidationFail() throws Exception {
        CreateProductRequest invalid = new CreateProductRequest(
                null,
                "",
                "",
                "",
                "https://example.com/thumbnail.jpg"
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}

