package inventory.inbound.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import inventory.common.exception.GlobalExceptionHandler;
import inventory.inbound.service.request.CreateInboundRequest;
import inventory.inbound.service.request.InboundProductRequest;
import inventory.inbound.service.request.UpdateInboundStatusRequest;
import inventory.inbound.service.response.InboundResponse;
import inventory.inbound.service.response.InboundSummaryResponse;
import inventory.inbound.service.response.InboundProductResponse;
import inventory.inbound.domain.Inbound;
import inventory.inbound.domain.enums.InboundStatus;
import inventory.inbound.service.InboundService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
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

    @MockitoBean
    private InboundService inboundService;

    @DisplayName("입고 등록을 성공하면 CREATED 상태와 입고 정보를 반환한다")
    @Test
    void createInboundWithSuccess() throws Exception {
        // given
        CreateInboundRequest request = new CreateInboundRequest(
                1L, 1L, LocalDate.now().plusDays(7),
                List.of(new InboundProductRequest(1L, 100))
        );

        InboundResponse savedInboundResponse = new InboundResponse(
                1L, 1L, "창고명", 1L, "공급업체명",
                LocalDate.now().plusDays(7),
                List.of(new InboundProductResponse(1L, "상품명", "P001", "개", 100)),
                InboundStatus.REGISTERED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(inboundService.save(any(CreateInboundRequest.class))).thenReturn(savedInboundResponse);

        // when & then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.name()))
                .andExpect(jsonPath("$.data.inboundId").value(1L))
                .andExpect(jsonPath("$.data.warehouseId").value(1L))
                .andExpect(jsonPath("$.data.supplierId").value(1L))
                .andExpect(jsonPath("$.data.products.length()").value(1))
                .andExpect(jsonPath("$.data.products[0].productId").value(1L))
                .andExpect(jsonPath("$.data.products[0].quantity").value(100))
                .andExpect(jsonPath("$.data.status").value(InboundStatus.REGISTERED.name()));
    }

    @DisplayName("입고 조회를 성공하면 OK 상태와 입고 정보를 반환한다")
    @Test
    void getInboundWithSuccess() throws Exception {
        // given
        Long inboundId = 1L;
        InboundResponse inboundResponse = new InboundResponse(
                inboundId, 1L, "창고명", 1L, "공급업체명",
                LocalDate.now().plusDays(7),
                List.of(new InboundProductResponse(1L, "상품명", "P001", "개", 100)),
                InboundStatus.REGISTERED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(inboundService.findById(inboundId)).thenReturn(inboundResponse);

        // when & then
        mockMvc.perform(get(BASE_URL + "/" + inboundId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.inboundId").value(inboundId))
                .andExpect(jsonPath("$.data.warehouseId").value(1L))
                .andExpect(jsonPath("$.data.supplierId").value(1L))
                .andExpect(jsonPath("$.data.products.length()").value(1))
                .andExpect(jsonPath("$.data.products[0].productId").value(1L))
                .andExpect(jsonPath("$.data.products[0].quantity").value(100));
    }

    @DisplayName("입고 목록 조회를 성공하면 페이징된 결과를 반환한다 (QueryDSL)")
    @Test
    void searchInboundsWithSuccess() throws Exception {
        // given
        InboundSummaryResponse summary = new InboundSummaryResponse(
                1L, 1L, "창고명", 1L, "공급업체명",
                LocalDate.now().plusDays(7), InboundStatus.REGISTERED,
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now()
        );
        Page<InboundSummaryResponse> page = new PageImpl<>(List.of(summary), Pageable.ofSize(10), 1);

        when(inboundService.findAllWithConditions(
                any(), any(), any(), any(), any(), any(Pageable.class)
        )).thenReturn(page);

        // when & then
        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.currentPageNumber").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].warehouseName").value("창고명"))
                .andExpect(jsonPath("$.data.content[0].supplierName").value("공급업체명"));
    }

    @DisplayName("입고 상태 업데이트를 성공하면 OK 상태와 업데이트된 입고 정보를 반환한다")
    @Test
    void updateInboundStatusWithSuccess() throws Exception {
        // given
        Long inboundId = 1L;
        UpdateInboundStatusRequest request = new UpdateInboundStatusRequest(InboundStatus.INSPECTING);
        
        InboundResponse updatedResponse = new InboundResponse(
                inboundId, 1L, "창고명", 1L, "공급업체명",
                LocalDate.now().plusDays(7),
                List.of(new InboundProductResponse(1L, "상품명", "P001", "개", 100)),
                InboundStatus.INSPECTING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(inboundService.updateStatus(eq(inboundId), any(UpdateInboundStatusRequest.class)))
                .thenReturn(updatedResponse);

        // when & then
        mockMvc.perform(put(BASE_URL + "/" + inboundId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.inboundId").value(inboundId))
                .andExpect(jsonPath("$.data.status").value(InboundStatus.INSPECTING.name()));
    }

    @DisplayName("입고 삭제를 성공하면 OK 상태를 반환한다")
    @Test
    void deleteInboundWithSuccess() throws Exception {
        // given
        Long inboundId = 1L;

        // when & then
        mockMvc.perform(delete(BASE_URL + "/" + inboundId))
                .andExpect(status().isNoContent());
    }
}
