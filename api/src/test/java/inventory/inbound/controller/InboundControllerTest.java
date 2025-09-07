package inventory.inbound.controller;

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
import inventory.inbound.controller.request.CreateInboundRequest;
import inventory.inbound.controller.request.InboundProductRequest;
import inventory.inbound.controller.request.UpdateInboundStatusRequest;
import inventory.inbound.domain.Inbound;
import inventory.inbound.domain.InboundProduct;
import inventory.inbound.enums.InboundStatus;
import inventory.inbound.service.InboundService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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

        InboundProduct product = new InboundProduct(1L, 100);
        Inbound savedInbound = Inbound.builder()
                .inboundId(1L)
                .warehouseId(1L)
                .supplierId(1L)
                .expectedDate(LocalDate.now().plusDays(7))
                .products(List.of(product))
                .status(InboundStatus.REGISTERED)
                .build();

        when(inboundService.save(any(CreateInboundRequest.class))).thenReturn(savedInbound);

        // when & then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.name()))
                .andExpect(jsonPath("$.data.id").value(1L))
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
        InboundProduct product = new InboundProduct(1L, 100);
        Inbound inbound = Inbound.builder()
                .inboundId(inboundId)
                .warehouseId(1L)
                .supplierId(1L)
                .expectedDate(LocalDate.now().plusDays(7))
                .products(List.of(product))
                .status(InboundStatus.REGISTERED)
                .build();

        when(inboundService.findById(inboundId)).thenReturn(inbound);

        // when & then
        mockMvc.perform(get(BASE_URL + "/" + inboundId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(inboundId))
                .andExpect(jsonPath("$.data.warehouseId").value(1L))
                .andExpect(jsonPath("$.data.supplierId").value(1L))
                .andExpect(jsonPath("$.data.products.length()").value(1))
                .andExpect(jsonPath("$.data.products[0].productId").value(1L))
                .andExpect(jsonPath("$.data.products[0].quantity").value(100));
    }

    @DisplayName("입고 목록 조회를 성공하면 페이징된 결과를 반환한다")
    @Test
    void searchInboundsWithSuccess() throws Exception {
        // given
        InboundProduct product = new InboundProduct(1L, 100);
        Inbound inbound = Inbound.builder()
                .inboundId(1L)
                .warehouseId(1L)
                .supplierId(1L)
                .expectedDate(LocalDate.now().plusDays(7))
                .products(List.of(product))
                .status(InboundStatus.REGISTERED)
                .build();

        when(inboundService.findAll()).thenReturn(List.of(inbound));

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

    @DisplayName("입고 상태 업데이트를 성공하면 OK 상태와 업데이트된 입고 정보를 반환한다")
    @Test
    void updateInboundStatusWithSuccess() throws Exception {
        // given
        Long inboundId = 1L;
        UpdateInboundStatusRequest request = new UpdateInboundStatusRequest(InboundStatus.INSPECTING);

        InboundProduct product = new InboundProduct(1L, 100);
        Inbound updatedInbound = Inbound.builder()
                .inboundId(inboundId)
                .warehouseId(1L)
                .supplierId(1L)
                .expectedDate(LocalDate.now().plusDays(7))
                .products(List.of(product))
                .status(InboundStatus.INSPECTING)
                .build();

        when(inboundService.updateStatus(eq(inboundId), any(UpdateInboundStatusRequest.class)))
                .thenReturn(updatedInbound);

        // when & then
        mockMvc.perform(put(BASE_URL + "/" + inboundId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(inboundId))
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
