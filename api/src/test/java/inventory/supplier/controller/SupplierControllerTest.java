package inventory.supplier.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import inventory.exception.GlobalExceptionHandler;
import inventory.supplier.controller.request.CreateSupplierRequest;
import inventory.supplier.controller.request.UpdateSupplierRequest;
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

@WebMvcTest(controllers = SupplierController.class)
@Import(GlobalExceptionHandler.class)
class SupplierControllerTest {

    private static final String BASE_URL = "/api/v1/suppliers";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        SupplierController.SUPPLIER_STORE.clear();
        SupplierController.ID_GENERATOR.set(1);
    }

    @DisplayName("공급업체 생성을 성공하면 CREATED 상태와 공급업체 정보를 반환한다.")
    @Test
    void createSupplierWithSuccess() throws Exception {
        CreateSupplierRequest request = createSupplierRequest();

        ResultActions result = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.name()))
                .andExpect(jsonPath("$.data.name").value("테스트 공급업체"))
                .andExpect(jsonPath("$.data.baseAddress").value("서울시 어딘가"));
    }

    private static CreateSupplierRequest createSupplierRequest() {
        return new CreateSupplierRequest(
                "테스트 공급업체",
                "1234567890",
                "12345",
                "서울시 어딘가",
                "상세주소",
                "김수용",
                "김매니저",
                "01012345678"
        );
    }

    @DisplayName("공급업체 페이징 조회를 성공하면 createdAt 역순으로 정렬하여 반환한다")
    @Test
    void searchSupplierWithSuccess() throws Exception {
        for (int i = 0; i < 10; i++) {
            CreateSupplierRequest request = new CreateSupplierRequest(
                    "테스트 공급업체" + i,
                    "123456789" + i,
                    "1234" + i,
                    "서울시 어딘가",
                    "상세주소",
                    "김수용",
                    "김매니저",
                    "0101234567" + i
            );

            mockMvc.perform(post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isCreated());
        }

        ResultActions result = mockMvc.perform(get(BASE_URL)
                .param("currentPageNumber", "0")
                .param("pageSize", "5")
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(5)) // 페이지 콘텐츠 크기는 5개
                .andExpect(jsonPath("$.data.currentPageNumber").value(0)) // 현재 페이지 번호는 0
                .andExpect(jsonPath("$.data.pageSize").value(5)) // 페이지 크기는 5
                .andExpect(jsonPath("$.data.totalElements").value(10)) // 전체 요소 수는 10
                .andExpect(jsonPath("$.data.hasNext").value(true)); // 0번 페이지니까 다음 페이지는 있음

        // createdAt 역순(내림차순) 검증하기 (첫 번째 항목이 두 번째 항목보다 나중에 생성되었는지 확인)
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

    @DisplayName("단일 공급업체 조회를 성공하면 해당 공급업체 정보를 반환한다")
    @Test
    void getSupplierWithSuccess() throws Exception {
        CreateSupplierRequest request = createSupplierRequest();

        String body = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        String id = JsonPath.read(body, "$.data.id").toString();

        mockMvc.perform(get(BASE_URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(Integer.parseInt(id))) // 요청 id와 동일한 id 반환
                .andExpect(jsonPath("$.data.name").value("테스트 공급업체")); // 생성 시 지정한 이름과 동일
    }

    @DisplayName("존재하지 않는 공급업체를 조회하면 404를 반환한다")
    @Test
    void getSupplierWithNotFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/9999"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("공급업체 정보 수정을 성공하면 수정된 정보를 반환한다")
    @Test
    void updateWarehouseWithSuccess() throws Exception {
        CreateSupplierRequest request = createSupplierRequest();

        String body = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        String id = JsonPath.read(body, "$.data.id").toString();

        UpdateSupplierRequest update = new UpdateSupplierRequest(
                "54321",
                "수정주소",
                "수정상세",
                "김관리",
                "수정",
                "01098765432"
        );

        mockMvc.perform(put(BASE_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.baseAddress").value("수정주소"))
                .andExpect(jsonPath("$.data.ceoName").value("김관리"));
    }
}