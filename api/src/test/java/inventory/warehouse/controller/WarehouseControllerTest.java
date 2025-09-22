package inventory.warehouse.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import inventory.common.exception.GlobalExceptionHandler;
import inventory.warehouse.controller.request.CreateWarehouseRequest;
import inventory.warehouse.controller.request.UpdateWarehouseRequest;
import inventory.warehouse.domain.Warehouse;
import inventory.warehouse.service.WarehouseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = WarehouseController.class)
@Import(GlobalExceptionHandler.class)
class WarehouseControllerTest {

    private static final String BASE_URL = "/api/v1/warehouses";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WarehouseService warehouseService;

    @DisplayName("창고 생성을 성공하면 CREATED 상태와 창고 정보를 반환한다")
    @Test
    void createWarehouseWithSuccess() throws Exception {
        // given
        CreateWarehouseRequest request = new CreateWarehouseRequest(
                "테스트용 창고",
                "12345",
                "서울시 용산구 청파로 40",
                "10층",
                "창고관리자",
                "01012345678"
        );

        Warehouse savedWarehouse = Warehouse.builder()
                .name("테스트용 창고")
                .postcode("12345")
                .baseAddress("서울시 용산구 청파로 40")
                .detailAddress("10층")
                .managerName("창고관리자")
                .managerContact("01012345678")
                .build();

        when(warehouseService.save(any(CreateWarehouseRequest.class))).thenReturn(savedWarehouse);

        // when & then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.name()))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("테스트용 창고"))
                .andExpect(jsonPath("$.data.postcode").value("12345"))
                .andExpect(jsonPath("$.data.baseAddress").value("서울시 용산구 청파로 40"))
                .andExpect(jsonPath("$.data.detailAddress").value("10층"))
                .andExpect(jsonPath("$.data.managerName").value("창고관리자"))
                .andExpect(jsonPath("$.data.managerContact").value("01012345678"));
    }

    @DisplayName("창고 조회를 성공하면 OK 상태와 창고 정보를 반환한다")
    @Test
    void getWarehouseWithSuccess() throws Exception {
        // given
        Long warehouseId = 1L;
        Warehouse warehouse = Warehouse.builder()
                .name("테스트 창고")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("어딘가동")
                .managerName("관리자")
                .managerContact("01011112222")
                .build();

        when(warehouseService.findById(warehouseId)).thenReturn(warehouse);

        // when & then
        mockMvc.perform(get(BASE_URL + "/" + warehouseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(warehouseId))
                .andExpect(jsonPath("$.data.name").value("테스트 창고"))
                .andExpect(jsonPath("$.data.postcode").value("12345"))
                .andExpect(jsonPath("$.data.baseAddress").value("서울시 어딘가"))
                .andExpect(jsonPath("$.data.detailAddress").value("어딘가동"))
                .andExpect(jsonPath("$.data.managerName").value("관리자"))
                .andExpect(jsonPath("$.data.managerContact").value("01011112222"));
    }

    @DisplayName("창고 목록 조회를 성공하면 페이징된 결과를 반환한다")
    @Test
    void searchWarehouseWithSuccess() throws Exception {
        // given
        Warehouse warehouse = Warehouse.builder()
                .name("테스트 창고")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("어딘가동")
                .managerName("관리자")
                .managerContact("01011112222")
                .build();

        when(warehouseService.findAll()).thenReturn(java.util.List.of(warehouse));

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

    @DisplayName("창고 정보 수정을 성공하면 수정된 정보를 반환한다")
    @Test
    void updateWarehouseWithSuccess() throws Exception {
        // given
        Long warehouseId = 1L;
        UpdateWarehouseRequest request = new UpdateWarehouseRequest(
                "수정된창고",
                "54321",
                "수정주소",
                "수정상세",
                "김관리",
                "01099998888"
        );

        Warehouse updatedWarehouse = Warehouse.builder()
                .name("수정된창고")
                .postcode("54321")
                .baseAddress("수정주소")
                .detailAddress("수정상세")
                .managerName("김관리")
                .managerContact("01099998888")
                .build();

        when(warehouseService.update(eq(warehouseId), any(UpdateWarehouseRequest.class)))
                .thenReturn(updatedWarehouse);

        // when & then
        mockMvc.perform(put(BASE_URL + "/" + warehouseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(warehouseId))
                .andExpect(jsonPath("$.data.name").value("수정된창고"))
                .andExpect(jsonPath("$.data.postcode").value("54321"))
                .andExpect(jsonPath("$.data.baseAddress").value("수정주소"))
                .andExpect(jsonPath("$.data.detailAddress").value("수정상세"))
                .andExpect(jsonPath("$.data.managerName").value("김관리"))
                .andExpect(jsonPath("$.data.managerContact").value("01099998888"));
    }

    @DisplayName("창고 삭제를 성공하면 NO_CONTENT 상태를 반환한다")
    @Test
    void deleteWarehouseWithSuccess() throws Exception {
        // given
        Long warehouseId = 1L;

        // when & then
        mockMvc.perform(delete(BASE_URL + "/" + warehouseId))
                .andExpect(status().isNoContent());
    }

    @DisplayName("유효하지 않은 창고 생성 요청 시 400을 반환한다")
    @Test
    void createWarehouseWithValidationFail() throws Exception {
        // given
        CreateWarehouseRequest invalidRequest = new CreateWarehouseRequest(
                "",
                "12",
                "",
                "",
                "",
                ""
        );

        // when & then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}