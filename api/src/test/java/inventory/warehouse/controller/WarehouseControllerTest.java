package inventory.warehouse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import inventory.exception.GlobalExceptionHandler;
import inventory.warehouse.controller.request.CreateWarehouseRequest;
import inventory.warehouse.controller.request.UpdateWarehouseRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WarehouseController.class)
@Import(GlobalExceptionHandler.class)
class WarehouseControllerTest {

    private static final String BASE_URL = "/api/v1/warehouses";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        WarehouseController.WAREHOUSE_STORE.clear();
        WarehouseController.ID_GENERATOR.set(1);
    }

    @DisplayName("창고 생성을 성공하면 상태코드 201과 ApiResponse 반환한다")
    @Test
    void createWarehouseWithSuccess() throws Exception {
        CreateWarehouseRequest request = new CreateWarehouseRequest(
                "테스트용 창고",
                "12345",
                "서울시 용산구 청파로 40",
                "10층",
                "창고관리자",
                "01012345678"
        );

        ResultActions result = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.name").value("테스트용 창고"))
                .andExpect(jsonPath("$.data.postcode").value("12345"))
                .andExpect(jsonPath("$.data.managerName").value("창고관리자"));
    }

    @DisplayName("창고 페이징 조회를 성공하면 createdAt 역순으로 정렬하여 반환한다")
    @Test
    void searchWarehouseWithSuccess() throws Exception {
        for (int i = 0; i < 10; i++) {
            CreateWarehouseRequest req = new CreateWarehouseRequest(
                    "창고" + i,
                    "1234" + i,
                    "주소" + i,
                    "상세" + i,
                    "관리자" + i,
                    "0100000000" + i
            );
            mockMvc.perform(post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req))
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

    @DisplayName("단일 창고 조회를 성공하면 해당 창고 정보를 반환한다")
    @Test
    void getWarehouseWithSuccess() throws Exception {
        CreateWarehouseRequest request = new CreateWarehouseRequest(
                "테스트 창고", "12345", "서울시 어딘가", "어딘가동", "관리자", "01011112222"
        );

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
                .andExpect(status().isOk()) // 200(OK)
                .andExpect(jsonPath("$.data.id").value(Integer.parseInt(id))) // 요청 id와 동일한 id 반환
                .andExpect(jsonPath("$.data.name").value("테스트 창고")); // 생성 시 지정한 이름과 동일
    }

    @DisplayName("존재하지 않는 창고를 조회하면 404를 반환한다")
    @Test
    void getWarehouseWithNotFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/9999"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("창고 정보 수정을 성공하면 수정된 정보를 반환한다")
    @Test
    void updateWarehouseWithSuccess() throws Exception {
        CreateWarehouseRequest request = new CreateWarehouseRequest(
                "수정전창고", "12345", "서울시 어딘가", "어딘가동", "관리자", "01011112222"
        );

        String body = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        String id = JsonPath.read(body, "$.data.id").toString();

        UpdateWarehouseRequest update = new UpdateWarehouseRequest(
                "수정된창고",
                "54321",
                "수정주소",
                "수정상세",
                "김관리",
                "01099998888"
        );

        mockMvc.perform(put(BASE_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("수정된창고"))
                .andExpect(jsonPath("$.data.postcode").value("54321"));
    }

    @DisplayName("창고 삭제를 성공하면 204를 반환한다")
    @Test
    void deleteWarehouseWithSuccess() throws Exception {
        CreateWarehouseRequest request = new CreateWarehouseRequest(
                "삭제할창고", "12345", "서울시 어딘가", "어딘가동", "관리자", "01011112222"
        );

        String body = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        String id = JsonPath.read(body, "$.data.id").toString();

        mockMvc.perform(delete(BASE_URL + "/" + id))
                .andExpect(status().isNoContent());
    }

    @DisplayName("유효하지 않은 창고 생성 요청을 하면 400을 반환한다")
    @Test
    void createWarehouseWithValidationFail() throws Exception {
        CreateWarehouseRequest invalid = new CreateWarehouseRequest(
                "",
                "12",
                "",
                "",
                "",
                ""
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}