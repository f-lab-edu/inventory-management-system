package inventory.warehouse.service;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.warehouse.domain.Warehouse;
import inventory.warehouse.repository.WarehouseRepository;
import inventory.warehouse.service.request.CreateWarehouseRequest;
import inventory.warehouse.service.request.UpdateWarehouseRequest;
import inventory.warehouse.service.response.WarehouseResponse;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class WarehouseServiceTest {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private WarehouseService warehouseService;

    @DisplayName("창고 저장을 성공하면 저장된 창고 정보를 반환한다")
    @Test
    void saveWithSuccess() {
        // given
        CreateWarehouseRequest request = new CreateWarehouseRequest(
                "테스트 창고",
                "12345",
                "서울시 강남구 테헤란로 123",
                "456호",
                "홍길동",
                "01012345678"
        );

        // when
        WarehouseResponse result = warehouseService.save(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("테스트 창고");
        assertThat(result.postcode()).isEqualTo("12345");
        assertThat(result.detailAddress()).isEqualTo("456호");
        assertThat(result.managerName()).isEqualTo("홍길동");
        assertThat(result.managerContact()).isEqualTo("01012345678");
        assertThat(result.active()).isTrue();
    }

    @DisplayName("상세주소가 null인 경우에도 창고 저장을 성공한다")
    @Test
    void saveWithNullDetailAddress() {
        // given
        CreateWarehouseRequest request = new CreateWarehouseRequest(
                "테스트 창고",
                "12345",
                "서울시 강남구 테헤란로 123",
                null,
                "홍길동",
                "01012345678"
        );

        // when
        WarehouseResponse result = warehouseService.save(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("테스트 창고");
        assertThat(result.postcode()).isEqualTo("12345");
        assertThat(result.detailAddress()).isNull();
        assertThat(result.active()).isTrue();
    }

    @DisplayName("상세주소가 빈 문자열인 경우에도 창고 저장을 성공한다")
    @Test
    void saveWithEmptyDetailAddress() {
        // given
        CreateWarehouseRequest request = new CreateWarehouseRequest(
                "테스트 창고",
                "12345",
                "서울시 강남구 테헤란로 123",
                "",
                "홍길동",
                "01012345678"
        );

        // when
        WarehouseResponse result = warehouseService.save(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("테스트 창고");
        assertThat(result.detailAddress()).isEmpty();
    }

    @DisplayName("ID로 창고 조회를 성공하면 해당 창고 정보를 반환한다")
    @Test
    void findByIdWithSuccess() {
        // given
        CreateWarehouseRequest request = new CreateWarehouseRequest(
                "테스트 창고",
                "12345",
                "서울시 강남구 테헤란로 123",
                "",
                "홍길동",
                "01012345678"
        );

        WarehouseResponse savedWarehouse = warehouseService.save(request);
        Long warehouseId = savedWarehouse.id();

        // when
        WarehouseResponse result = warehouseService.findById(warehouseId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(warehouseId);
        assertThat(result.name()).isEqualTo("테스트 창고");
        assertThat(result.postcode()).isEqualTo("12345");
        assertThat(result.managerName()).isEqualTo("홍길동");
        assertThat(result.managerContact()).isEqualTo("01012345678");
    }

    @DisplayName("존재하지 않는 ID로 창고 조회 시 예외가 발생한다")
    @Test
    void findByIdWithNotFound() {
        // given
        Long warehouseId = 999L;

        // when & then
        assertThatThrownBy(() -> warehouseService.findById(warehouseId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);
    }

    @DisplayName("null ID로 창고 조회 시 예외가 발생한다")
    @Test
    void findByIdWithNullId() {
        // when & then
        assertThatThrownBy(() -> warehouseService.findById(null))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.INVALID_INPUT);
    }

    @DisplayName("창고 목록 조회를 성공하면 페이징 결과를 반환한다")
    @Test
    void findAllWithSuccess() {
        // given
        CreateWarehouseRequest request1 = new CreateWarehouseRequest(
                "테스트 창고1",
                "12345",
                "서울시 강남구 테헤란로 123",
                "",
                "홍길동",
                "01012345678"
        );

        CreateWarehouseRequest request2 = new CreateWarehouseRequest(
                "테스트 창고2",
                "54321",
                "서울시 강남구 테헤란로 321",
                "",
                "김수용",
                "01056781234"
        );

        warehouseService.save(request1);
        warehouseService.save(request2);

        // when
        Page<WarehouseResponse> page = warehouseService.findAllWithConditions(
                null, null, null, PageRequest.of(0, 10)
        );

        // then
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(2);
        assertThat(page.getContent().getFirst().name()).isEqualTo("테스트 창고2");
    }

    @DisplayName("창고 정보 수정을 성공하면 수정된 창고 정보를 반환한다")
    @Test
    void updateWithSuccess() {
        // given
        CreateWarehouseRequest request = new CreateWarehouseRequest(
                "테스트 창고",
                "12345",
                "서울시 강남구 테헤란로 123",
                "456호",
                "홍길동",
                "01012345678"
        );

        WarehouseResponse savedWarehouse = warehouseService.save(request);

        UpdateWarehouseRequest updateRequest = new UpdateWarehouseRequest(
                "수정된 창고",
                "54321",
                "서울시 서초구 테헤란로 456",
                "789호",
                "김관리",
                "01098765432"
        );

        // when
        WarehouseResponse result = warehouseService.update(savedWarehouse.id(), updateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("수정된 창고");
        assertThat(result.postcode()).isEqualTo("54321");
        assertThat(result.baseAddress()).isEqualTo("서울시 서초구 테헤란로 456");
        assertThat(result.detailAddress()).isEqualTo("789호");
        assertThat(result.managerName()).isEqualTo("김관리");
        assertThat(result.managerContact()).isEqualTo("01098765432");
    }

    @DisplayName("존재하지 않는 창고 수정 시 예외가 발생한다")
    @Test
    void updateWithNotFound() {
        // given
        Long warehouseId = 999L;
        UpdateWarehouseRequest updateRequest = new UpdateWarehouseRequest(
                "수정된 창고",
                "54321",
                "서울시 어딘가",
                "789호",
                "김관리",
                "01098765432"
        );

        // when & then
        assertThatThrownBy(() -> warehouseService.update(warehouseId, updateRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);

    }

    @DisplayName("창고 삭제를 성공한다")
    @Test
    void deleteByIdWithSuccess() {
        // given
        CreateWarehouseRequest request = new CreateWarehouseRequest(
                "테스트 창고",
                "12345",
                "서울시 강남구 테헤란로 123",
                "456호",
                "홍길동",
                "01012345678"
        );

        WarehouseResponse savedWarehouse = warehouseService.save(request);

        // when
        warehouseService.deleteById(savedWarehouse.id());

        // then
        Warehouse warehouse = warehouseRepository.findById(savedWarehouse.id())
                .orElse(null);
        assertThat(warehouse).isNotNull();
        assertThat(warehouse.isDeleted()).isTrue();
    }

    @DisplayName("존재하지 않는 창고 삭제 시 예외가 발생한다")
    @Test
    void deleteByIdWithNotFound() {
        // given
        Long warehouseId = 999L;

        // when & then
        assertThatThrownBy(() -> warehouseService.deleteById(warehouseId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);
    }
}
