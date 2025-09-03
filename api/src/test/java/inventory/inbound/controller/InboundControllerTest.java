package inventory.inbound.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import inventory.common.exception.GlobalExceptionHandler;
import inventory.inbound.controller.request.CreateInboundRequest;
import inventory.inbound.controller.request.UpdateInboundStatusRequest;
import inventory.inbound.enums.InboundStatus;
import inventory.product.controller.ProductController;
import inventory.product.controller.response.ProductResponse;
import inventory.supplier.controller.SupplierController;
import inventory.supplier.controller.response.SupplierResponse;
import inventory.warehouse.controller.WarehouseController;
import inventory.warehouse.controller.response.WarehouseResponse;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InboundController.class)
@Import(GlobalExceptionHandler.class)
class InboundControllerTest {

    private static final String BASE_URL = "/api/v1/inbounds";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long testSupplierId;
    private String testSupplierName;
    private Long testWarehouseId;
    private String testWarehouseName;
    private Long testProductId;
    private String testProductName;
    private Long testProductId2;
    private String testProductName2;

    @BeforeEach
    void setUp() {
        // 모든 저장소 초기화
        InboundController.WAREHOUSE_PRODUCT_STORE.clear();
        InboundController.INBOUND_STORE.clear();
        InboundController.INBOUND_ID_GENERATOR.set(1);
        InboundController.WAREHOUSE_PRODUCT_ID_GENERATOR.set(1);

        SupplierController.SUPPLIER_STORE.clear();
        WarehouseController.WAREHOUSE_STORE.clear();
        ProductController.PRODUCT_STORE.clear();
        ProductController.ID_GENERATOR.set(1);

        // 테스트용 데이터 생성
        createTestData();
    }

    private void createTestData() {
        // 공급업체 생성
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

        // 창고 생성
        testWarehouseId = 1L;
        testWarehouseName = "테스트 창고";

        WarehouseResponse warehouseResponse = WarehouseResponse.of(
                testWarehouseId,
                testWarehouseName,
                "12345",
                "서울시 어딘가",
                "상세주소",
                "창고관리자",
                "01012345678",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        WarehouseController.WAREHOUSE_STORE.put(testWarehouseId, warehouseResponse);

        // 상품 1 생성
        testProductId = 1L;
        testProductName = "테스트 상품 1";

        ProductResponse productResponse1 = ProductResponse.of(
                testProductId,
                testProductName,
                testSupplierId,
                testSupplierName,
                "PROD001",
                "https://example.com/thumbnail1.jpg",
                "EA",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        ProductController.PRODUCT_STORE.put(testProductId, productResponse1);

        // 상품 2 생성
        testProductId2 = 2L;
        testProductName2 = "테스트 상품 2";

        ProductResponse productResponse2 = ProductResponse.of(
                testProductId2,
                testProductName2,
                testSupplierId,
                testSupplierName,
                "PROD002",
                "https://example.com/thumbnail2.jpg",
                "BOX",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        ProductController.PRODUCT_STORE.put(testProductId2, productResponse2);
    }

    @DisplayName("단일 상품 입고 등록을 성공하면 OK 상태와 입고 정보를 반환한다")
    @Test
    void createSingleInboundWithSuccess() throws Exception {
        CreateInboundRequest request = new CreateInboundRequest(
                testWarehouseId,
                testSupplierId,
                LocalDate.now().plusDays(7),
                List.of(
                        new CreateInboundRequest.InboundProductRequest(testProductId, 100)
                )
        );

        ResultActions result = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.name()))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].warehouseId").value(testWarehouseId))
                .andExpect(jsonPath("$.data[0].warehouseName").value(testWarehouseName))
                .andExpect(jsonPath("$.data[0].productId").value(testProductId))
                .andExpect(jsonPath("$.data[0].productName").value(testProductName))
                .andExpect(jsonPath("$.data[0].supplierId").value(testSupplierId))
                .andExpect(jsonPath("$.data[0].supplierName").value(testSupplierName))
                .andExpect(jsonPath("$.data[0].quantity").value(100))
                .andExpect(jsonPath("$.data[0].status").value(InboundStatus.REGISTERED.name()));
    }

    @DisplayName("다중 상품 입고 등록을 성공하면 OK 상태와 모든 입고 정보를 반환한다")
    @Test
    void createMultipleInboundsWithSuccess() throws Exception {
        CreateInboundRequest request = new CreateInboundRequest(
                testWarehouseId,
                testSupplierId,
                LocalDate.now().plusDays(7),
                List.of(
                        new CreateInboundRequest.InboundProductRequest(testProductId, 100),
                        new CreateInboundRequest.InboundProductRequest(testProductId2, 50)
                )
        );

        ResultActions result = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.name()))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].productId").value(testProductId))
                .andExpect(jsonPath("$.data[0].quantity").value(100))
                .andExpect(jsonPath("$.data[1].productId").value(testProductId2))
                .andExpect(jsonPath("$.data[1].quantity").value(50));
    }

    @DisplayName("존재하지 않는 창고로 입고 등록 시 404를 반환한다")
    @Test
    void createInboundWithNonExistentWarehouse() throws Exception {
        CreateInboundRequest request = new CreateInboundRequest(
                999L,
                testSupplierId,
                LocalDate.now().plusDays(7),
                List.of(
                        new CreateInboundRequest.InboundProductRequest(testProductId, 100)
                )
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @DisplayName("존재하지 않는 상품으로 입고 등록 시 404를 반환한다")
    @Test
    void createInboundWithNonExistentProduct() throws Exception {
        CreateInboundRequest request = new CreateInboundRequest(
                testWarehouseId,
                testSupplierId,
                LocalDate.now().plusDays(7),
                List.of(
                        new CreateInboundRequest.InboundProductRequest(999L, 100)
                )
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @DisplayName("존재하지 않는 공급업체로 입고 등록 시 404를 반환한다")
    @Test
    void createInboundWithNonExistentSupplier() throws Exception {
        CreateInboundRequest request = new CreateInboundRequest(
                testWarehouseId,
                999L,
                LocalDate.now().plusDays(7),
                List.of(
                        new CreateInboundRequest.InboundProductRequest(testProductId, 100)
                )
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @DisplayName("입고 상태를 검수 중으로 변경하면 성공한다")
    @Test
    void updateInboundStatusToInspecting() throws Exception {
        // 먼저 입고 등록
        CreateInboundRequest createRequest = new CreateInboundRequest(
                testWarehouseId,
                testSupplierId,
                LocalDate.now().plusDays(7),
                List.of(
                        new CreateInboundRequest.InboundProductRequest(testProductId, 100)
                )
        );

        String createBody = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Long inboundId = Long.parseLong(JsonPath.read(createBody, "$.data[0].id").toString());

        // 상태를 검수 중으로 변경
        UpdateInboundStatusRequest updateRequest = new UpdateInboundStatusRequest(InboundStatus.INSPECTING);

        mockMvc.perform(put(BASE_URL + "/" + inboundId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(InboundStatus.INSPECTING.name()));
    }

    @DisplayName("입고 상태를 검수 완료로 변경하면 재고가 증가한다")
    @Test
    void updateInboundStatusToCompleted() throws Exception {
        CreateInboundRequest createRequest = new CreateInboundRequest(
                testWarehouseId,
                testSupplierId,
                LocalDate.now().plusDays(7),
                List.of(
                        new CreateInboundRequest.InboundProductRequest(testProductId, 100)
                )
        );

        String createBody = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        long inboundId = Long.parseLong(JsonPath.read(createBody, "$.data[0].id").toString());

        UpdateInboundStatusRequest updateRequest = new UpdateInboundStatusRequest(InboundStatus.COMPLETED);

        mockMvc.perform(put(BASE_URL + "/" + inboundId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(InboundStatus.COMPLETED.name()));

        mockMvc.perform(get(BASE_URL + "/warehouses/" + testWarehouseId + "/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].currentStock").value(100))
                .andExpect(jsonPath("$.data[0].safetyStock").value(10));
    }

    @DisplayName("입고 목록 조회를 성공하면 페이징된 결과를 반환한다")
    @Test
    void searchInboundsWithSuccess() throws Exception {
        // 여러 입고 등록
        for (int i = 0; i < 5; i++) {
            CreateInboundRequest request = new CreateInboundRequest(
                    testWarehouseId,
                    testSupplierId,
                    LocalDate.now().plusDays(i + 1),
                    List.of(
                            new CreateInboundRequest.InboundProductRequest(testProductId, 50 + i * 10)
                    )
            );

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        ResultActions result = mockMvc.perform(get(BASE_URL)
                .param("currentPageNumber", "0")
                .param("pageSize", "3")
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(3))
                .andExpect(jsonPath("$.data.currentPageNumber").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(3))
                .andExpect(jsonPath("$.data.totalElements").value(5))
                .andExpect(jsonPath("$.data.hasNext").value(true));
    }

    @DisplayName("단일 입고 조회를 성공하면 해당 입고 정보를 반환한다")
    @Test
    void getInboundWithSuccess() throws Exception {
        // 입고 등록
        CreateInboundRequest request = new CreateInboundRequest(
                testWarehouseId,
                testSupplierId,
                LocalDate.now().plusDays(7),
                List.of(
                        new CreateInboundRequest.InboundProductRequest(testProductId, 100)
                )
        );

        String createBody = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Long inboundId = Long.parseLong(JsonPath.read(createBody, "$.data[0].id").toString());

        // 단일 입고 조회
        mockMvc.perform(get(BASE_URL + "/" + inboundId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(inboundId))
                .andExpect(jsonPath("$.data.warehouseId").value(testWarehouseId))
                .andExpect(jsonPath("$.data.productId").value(testProductId))
                .andExpect(jsonPath("$.data.supplierId").value(testSupplierId));
    }

    @DisplayName("존재하지 않는 입고를 조회하면 404를 반환한다")
    @Test
    void getInboundWithNotFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/9999"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("창고별 입고 내역 조회를 성공하면 해당 창고의 입고 목록을 반환한다")
    @Test
    void getInboundsByWarehouseWithSuccess() throws Exception {
        // 여러 입고 등록
        for (int i = 0; i < 3; i++) {
            CreateInboundRequest request = new CreateInboundRequest(
                    testWarehouseId,
                    testSupplierId,
                    LocalDate.now().plusDays(i + 1),
                    List.of(
                            new CreateInboundRequest.InboundProductRequest(testProductId, 50 + i * 10)
                    )
            );

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        // 창고별 입고 내역 조회
        mockMvc.perform(get(BASE_URL + "/warehouses/" + testWarehouseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].warehouseId").value(testWarehouseId))
                .andExpect(jsonPath("$.data[0].warehouseName").value(testWarehouseName));
    }

    @DisplayName("상품별 입고 내역 조회를 성공하면 해당 상품의 입고 목록을 반환한다")
    @Test
    void getInboundsByProductWithSuccess() throws Exception {
        // 여러 입고 등록
        for (int i = 0; i < 3; i++) {
            CreateInboundRequest request = new CreateInboundRequest(
                    testWarehouseId,
                    testSupplierId,
                    LocalDate.now().plusDays(i + 1),
                    List.of(
                            new CreateInboundRequest.InboundProductRequest(testProductId, 50 + i * 10)
                    )
            );

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        // 상품별 입고 내역 조회
        mockMvc.perform(get(BASE_URL + "/products/" + testProductId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].productId").value(testProductId))
                .andExpect(jsonPath("$.data[0].productName").value(testProductName));
    }

    @DisplayName("공급업체별 입고 내역 조회를 성공하면 해당 공급업체의 입고 목록을 반환한다")
    @Test
    void getInboundsBySupplierWithSuccess() throws Exception {
        // 여러 입고 등록
        for (int i = 0; i < 3; i++) {
            CreateInboundRequest request = new CreateInboundRequest(
                    testWarehouseId,
                    testSupplierId,
                    LocalDate.now().plusDays(i + 1),
                    List.of(
                            new CreateInboundRequest.InboundProductRequest(testProductId, 50 + i * 10)
                    )
            );

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get(BASE_URL + "/suppliers/" + testSupplierId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].supplierId").value(testSupplierId))
                .andExpect(jsonPath("$.data[0].supplierName").value(testSupplierName));
    }
}
