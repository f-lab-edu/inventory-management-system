package inventory.supplier.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import inventory.common.exception.GlobalExceptionHandler;
import inventory.supplier.controller.request.CreateSupplierRequest;
import inventory.supplier.controller.request.UpdateSupplierRequest;
import inventory.supplier.controller.response.SupplierResponse;
import inventory.supplier.domain.Supplier;
import inventory.supplier.service.SupplierService;
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

@WebMvcTest(controllers = SupplierController.class)
@Import(GlobalExceptionHandler.class)
class SupplierControllerTest {

    private static final String BASE_URL = "/api/v1/suppliers";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SupplierService supplierService;

    @DisplayName("공급업체 생성을 성공하면 CREATED 상태와 공급업체 정보를 반환한다")
    @Test
    void createSupplierWithSuccess() throws Exception {
        // given
        CreateSupplierRequest request = new CreateSupplierRequest(
                "테스트 공급업체",
                "1234567890",
                "12345",
                "서울시 어딘가",
                "상세주소",
                "김수용",
                "김매니저",
                "01012345678"
        );
        Supplier supplier = Supplier.builder()
                .name("테스트 공급업체")
                .businessRegistrationNumber("1234567890")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("상세주소")
                .ceoName("김수용")
                .managerName("김매니저")
                .managerContact("01012345678")
                .build();

        when(supplierService.save(any(CreateSupplierRequest.class)))
                .thenReturn(SupplierResponse.from(supplier));

        // when & then
        mockMvc.perform(
                        post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.name()))
                .andExpect(jsonPath("$.data.name").value("테스트 공급업체"))
                .andExpect(jsonPath("$.data.businessRegistrationNumber").value("1234567890"))
                .andExpect(jsonPath("$.data.postcode").value("12345"))
                .andExpect(jsonPath("$.data.baseAddress").value("서울시 어딘가"))
                .andExpect(jsonPath("$.data.detailAddress").value("상세주소"))
                .andExpect(jsonPath("$.data.ceoName").value("김수용"))
                .andExpect(jsonPath("$.data.managerName").value("김매니저"))
                .andExpect(jsonPath("$.data.managerContact").value("01012345678"));
    }

    @DisplayName("공급업체 조회를 성공하면 OK 상태와 공급업체 정보를 반환한다")
    @Test
    void getSupplierWithSuccess() throws Exception {
        // given
        Long supplierId = 1L;
        Supplier supplier = Supplier.builder()
                .name("테스트 공급업체")
                .businessRegistrationNumber("1234567890")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("상세주소")
                .ceoName("김수용")
                .managerName("김매니저")
                .managerContact("01012345678")
                .build();

        when(supplierService.findById(supplierId)).thenReturn(SupplierResponse.from(supplier));

        // when & then
        mockMvc.perform(get(BASE_URL + "/" + supplierId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("테스트 공급업체"))
                .andExpect(jsonPath("$.data.businessRegistrationNumber").value("1234567890"))
                .andExpect(jsonPath("$.data.postcode").value("12345"))
                .andExpect(jsonPath("$.data.baseAddress").value("서울시 어딘가"));
    }

    @DisplayName("공급업체 목록 조회를 성공하면 페이징된 결과를 반환한다")
    @Test
    void searchSupplierWithSuccess() throws Exception {
        // given
        Supplier supplier = Supplier.builder()
                .name("테스트 공급업체")
                .businessRegistrationNumber("1234567890")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("상세주소")
                .ceoName("김수용")
                .managerName("김매니저")
                .managerContact("01012345678")
                .build();

        when(supplierService.findAll()).thenReturn(List.of(SupplierResponse.from(supplier)));

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

    @DisplayName("공급업체 정보 수정을 성공하면 수정된 정보를 반환한다")
    @Test
    void updateSupplierWithSuccess() throws Exception {
        // given
        Long supplierId = 1L;
        UpdateSupplierRequest request = new UpdateSupplierRequest(
                "54321",
                "수정주소",
                "수정상세",
                "김관리",
                "수정매니저",
                "01098765432"
        );

        Supplier updatedSupplier = Supplier.builder()
                .name("테스트 공급업체")
                .businessRegistrationNumber("1234567890")
                .postcode("54321")
                .baseAddress("수정주소")
                .detailAddress("수정상세")
                .ceoName("김관리")
                .managerName("수정매니저")
                .managerContact("01098765432")
                .build();

        when(supplierService.update(eq(supplierId), any(UpdateSupplierRequest.class)))
                .thenReturn(SupplierResponse.from(updatedSupplier));

        // when & then
        mockMvc.perform(put(BASE_URL + "/" + supplierId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.postcode").value("54321"))
                .andExpect(jsonPath("$.data.baseAddress").value("수정주소"))
                .andExpect(jsonPath("$.data.detailAddress").value("수정상세"))
                .andExpect(jsonPath("$.data.ceoName").value("김관리"))
                .andExpect(jsonPath("$.data.managerName").value("수정매니저"))
                .andExpect(jsonPath("$.data.managerContact").value("01098765432"));
    }

    @DisplayName("공급업체 삭제를 성공하면 NO_CONTENT 상태를 반환한다")
    @Test
    void deleteSupplierWithSuccess() throws Exception {
        // given
        Long supplierId = 1L;

        // when & then
        mockMvc.perform(delete(BASE_URL + "/" + supplierId))
                .andExpect(status().isNoContent());
    }
}