package inventory.warehouse.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.warehouse.controller.request.CreateWarehouseRequest;
import inventory.warehouse.controller.request.UpdateWarehouseRequest;
import inventory.warehouse.domain.Warehouse;
import inventory.warehouse.repository.WarehouseRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
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

        Warehouse expectedWarehouse = Warehouse.builder()
                .name("테스트 창고")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("456호")
                .managerName("홍길동")
                .managerContact("01012345678")
                .build();

        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(expectedWarehouse);

        // when
        Warehouse result = warehouseService.save(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("테스트 창고");
        assertThat(result.getPostcode()).isEqualTo("12345");
        assertThat(result.getBaseAddress()).isEqualTo("서울시 어딘가");
        assertThat(result.getDetailAddress()).isEqualTo("456호");
        assertThat(result.getManagerName()).isEqualTo("홍길동");
        assertThat(result.getManagerContact()).isEqualTo("01012345678");
        assertThat(result.isActive()).isTrue();

        verify(warehouseRepository).save(any(Warehouse.class));
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

        Warehouse expectedWarehouse = Warehouse.builder()
                .name("테스트 창고")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress(null)
                .managerName("홍길동")
                .managerContact("01012345678")
                .build();

        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(expectedWarehouse);

        // when
        Warehouse result = warehouseService.save(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("테스트 창고");
        assertThat(result.getPostcode()).isEqualTo("12345");
        assertThat(result.getBaseAddress()).isEqualTo("서울시 어딘가");
        assertThat(result.getDetailAddress()).isNull();
        assertThat(result.getManagerName()).isEqualTo("홍길동");
        assertThat(result.getManagerContact()).isEqualTo("01012345678");
        assertThat(result.isActive()).isTrue();

        verify(warehouseRepository).save(any(Warehouse.class));
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

        Warehouse expectedWarehouse = Warehouse.builder()
                .name("테스트 창고")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("")
                .managerName("홍길동")
                .managerContact("01012345678")
                .build();

        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(expectedWarehouse);

        // when
        Warehouse result = warehouseService.save(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("테스트 창고");
        assertThat(result.getPostcode()).isEqualTo("12345");
        assertThat(result.getBaseAddress()).isEqualTo("서울시 어딘가");
        assertThat(result.getDetailAddress()).isEmpty();
        assertThat(result.getManagerName()).isEqualTo("홍길동");
        assertThat(result.getManagerContact()).isEqualTo("01012345678");
        assertThat(result.isActive()).isTrue();

        verify(warehouseRepository).save(any(Warehouse.class));
    }

    @DisplayName("ID로 창고 조회를 성공하면 해당 창고 정보를 반환한다")
    @Test
    void findByIdWithSuccess() {
        // given
        Long warehouseId = 1L;
        Warehouse expectedWarehouse = Warehouse.builder()
                .warehouseId(warehouseId)
                .name("테스트 창고")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("456호")
                .managerName("홍길동")
                .managerContact("01012345678")
                .build();

        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(expectedWarehouse));

        // when
        Warehouse result = warehouseService.findById(warehouseId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getWarehouseId()).isEqualTo(warehouseId);
        assertThat(result.getName()).isEqualTo("테스트 창고");
        assertThat(result.getPostcode()).isEqualTo("12345");
        assertThat(result.getBaseAddress()).isEqualTo("서울시 어딘가");
        assertThat(result.getDetailAddress()).isEqualTo("456호");
        assertThat(result.getManagerName()).isEqualTo("홍길동");
        assertThat(result.getManagerContact()).isEqualTo("01012345678");

        verify(warehouseRepository).findById(warehouseId);
    }

    @DisplayName("존재하지 않는 ID로 창고 조회 시 예외가 발생한다")
    @Test
    void findByIdWithNotFound() {
        // given
        Long warehouseId = 999L;
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> warehouseService.findById(warehouseId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);

        verify(warehouseRepository).findById(warehouseId);
    }

    @DisplayName("null ID로 창고 조회 시 예외가 발생한다")
    @Test
    void findByIdWithNullId() {
        // when & then
        assertThatThrownBy(() -> warehouseService.findById(null))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.INVALID_INPUT);
    }

    @DisplayName("모든 창고 조회를 성공하면 창고 목록을 반환한다")
    @Test
    void findAllWithSuccess() {
        // given
        Warehouse warehouse1 = Warehouse.builder()
                .warehouseId(1L)
                .name("창고1")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("1호")
                .managerName("관리자1")
                .managerContact("01011111111")
                .build();

        Warehouse warehouse2 = Warehouse.builder()
                .warehouseId(2L)
                .name("창고2")
                .postcode("54321")
                .baseAddress("서울시 어딘가")
                .detailAddress("2호")
                .managerName("관리자2")
                .managerContact("01022222222")
                .build();

        List<Warehouse> expectedWarehouses = List.of(warehouse1, warehouse2);
        when(warehouseRepository.findAll()).thenReturn(expectedWarehouses);

        // when
        List<Warehouse> result = warehouseService.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("창고1");
        assertThat(result.get(1).getName()).isEqualTo("창고2");

        verify(warehouseRepository).findAll();
    }

    @DisplayName("창고 정보 수정을 성공하면 수정된 창고 정보를 반환한다")
    @Test
    void updateWithSuccess() {
        // given
        Long warehouseId = 1L;
        UpdateWarehouseRequest updateRequest = new UpdateWarehouseRequest(
                "수정된 창고",
                "54321",
                "서울시 서초구 테헤란로 456",
                "789호",
                "김관리",
                "01098765432"
        );

        Warehouse existingWarehouse = Warehouse.builder()
                .warehouseId(warehouseId)
                .name("기존 창고")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("456호")
                .managerName("홍길동")
                .managerContact("01012345678")
                .build();

        Warehouse updatedWarehouse = Warehouse.builder()
                .warehouseId(warehouseId)
                .name("수정된 창고")
                .postcode("54321")
                .baseAddress("서울시 어딘가")
                .detailAddress("789호")
                .managerName("김관리")
                .managerContact("01098765432")
                .build();

        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(existingWarehouse));
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(updatedWarehouse);

        // when
        Warehouse result = warehouseService.update(warehouseId, updateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getWarehouseId()).isEqualTo(warehouseId);
        assertThat(result.getName()).isEqualTo("수정된 창고");
        assertThat(result.getPostcode()).isEqualTo("54321");
        assertThat(result.getBaseAddress()).isEqualTo("서울시 서초구 테헤란로 456");
        assertThat(result.getDetailAddress()).isEqualTo("789호");
        assertThat(result.getManagerName()).isEqualTo("김관리");
        assertThat(result.getManagerContact()).isEqualTo("01098765432");

        verify(warehouseRepository).findById(warehouseId);
        verify(warehouseRepository).save(any(Warehouse.class));
    }

    @DisplayName("부분 수정을 성공하면 기존 값과 수정된 값이 함께 반환된다")
    @Test
    void updateWithPartialSuccess() {
        // given
        Long warehouseId = 1L;
        UpdateWarehouseRequest updateRequest = new UpdateWarehouseRequest(
                "수정된 창고",
                null, // postcode는 수정하지 않음
                null, // baseAddress는 수정하지 않음
                "789호",
                null, // managerName은 수정하지 않음
                null  // managerContact는 수정하지 않음
        );

        Warehouse existingWarehouse = Warehouse.builder()
                .warehouseId(warehouseId)
                .name("기존 창고")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("456호")
                .managerName("홍길동")
                .managerContact("01012345678")
                .build();

        Warehouse updatedWarehouse = Warehouse.builder()
                .warehouseId(warehouseId)
                .name("수정된 창고")
                .postcode("12345") // 기존 값 유지
                .baseAddress("서울시 어딘가") // 기존 값 유지
                .detailAddress("789호") // 수정된 값
                .managerName("홍길동") // 기존 값 유지
                .managerContact("01012345678") // 기존 값 유지
                .build();

        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(existingWarehouse));
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(updatedWarehouse);

        // when
        Warehouse result = warehouseService.update(warehouseId, updateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("수정된 창고");
        assertThat(result.getPostcode()).isEqualTo("12345");
        assertThat(result.getBaseAddress()).isEqualTo("서울시 어딘가");
        assertThat(result.getDetailAddress()).isEqualTo("789호");
        assertThat(result.getManagerName()).isEqualTo("홍길동");
        assertThat(result.getManagerContact()).isEqualTo("01012345678");

        verify(warehouseRepository).findById(warehouseId);
        verify(warehouseRepository).save(any(Warehouse.class));
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

        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> warehouseService.update(warehouseId, updateRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);

        verify(warehouseRepository).findById(warehouseId);
    }

    @DisplayName("null ID로 창고 수정 시 예외가 발생한다")
    @Test
    void updateWithNullId() {
        // given
        UpdateWarehouseRequest updateRequest = new UpdateWarehouseRequest(
                "수정된 창고",
                "54321",
                "서울시 서초구 테헤란로 456",
                "789호",
                "김관리",
                "01098765432"
        );

        // when & then
        assertThatThrownBy(() -> warehouseService.update(null, updateRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.INVALID_INPUT);
    }

    @DisplayName("창고 삭제를 성공한다")
    @Test
    void deleteByIdWithSuccess() {
        // given
        Long warehouseId = 1L;
        Warehouse existingWarehouse = Warehouse.builder()
                .warehouseId(warehouseId)
                .name("삭제할 창고")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("456호")
                .managerName("홍길동")
                .managerContact("01012345678")
                .build();

        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(existingWarehouse));

        // when
        warehouseService.deleteById(warehouseId);

        // then
        verify(warehouseRepository).findById(warehouseId);
        verify(warehouseRepository).deleteById(warehouseId);
    }

    @DisplayName("존재하지 않는 창고 삭제 시 예외가 발생한다")
    @Test
    void deleteByIdWithNotFound() {
        // given
        Long warehouseId = 999L;
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> warehouseService.deleteById(warehouseId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);

        verify(warehouseRepository).findById(warehouseId);
    }

    @DisplayName("null ID로 창고 삭제 시 예외가 발생한다")
    @Test
    void deleteByIdWithNullId() {
        // when & then
        assertThatThrownBy(() -> warehouseService.deleteById(null))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.INVALID_INPUT);
    }
}
