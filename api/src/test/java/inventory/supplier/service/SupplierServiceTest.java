package inventory.supplier.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.supplier.controller.request.CreateSupplierRequest;
import inventory.supplier.controller.request.UpdateSupplierRequest;
import inventory.supplier.domain.Supplier;
import inventory.supplier.repository.SupplierRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierService supplierService;

    @DisplayName("공급업체 저장을 성공하면 저장된 공급업체 정보를 반환한다")
    @Test
    void saveWithSuccess() {
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

        Supplier expectedSupplier = Supplier.builder()
                .supplierId(1L)
                .name("테스트 공급업체")
                .businessRegistrationNumber("1234567890")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("상세주소")
                .ceoName("김수용")
                .managerName("김매니저")
                .managerContact("01012345678")
                .build();

        when(supplierRepository.save(any(Supplier.class))).thenReturn(expectedSupplier);

        // when
        Supplier result = supplierService.save(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("테스트 공급업체");
        assertThat(result.getBusinessRegistrationNumber()).isEqualTo("1234567890");
        assertThat(result.getPostcode()).isEqualTo("12345");
        assertThat(result.getBaseAddress()).isEqualTo("서울시 어딘가");
        assertThat(result.getDetailAddress()).isEqualTo("상세주소");
        assertThat(result.getCeoName()).isEqualTo("김수용");
        assertThat(result.getManagerName()).isEqualTo("김매니저");
        assertThat(result.getManagerContact()).isEqualTo("01012345678");
        assertThat(result.isActive()).isTrue();

        verify(supplierRepository).save(any(Supplier.class));
    }

    @DisplayName("상세주소가 null인 경우에도 공급업체 저장을 성공한다")
    @Test
    void saveWithNullDetailAddress() {
        // given
        CreateSupplierRequest request = new CreateSupplierRequest(
                "테스트 공급업체",
                "1234567890",
                "12345",
                "서울시 어딘가",
                null,
                "김수용",
                "김매니저",
                "01012345678"
        );

        Supplier expectedSupplier = Supplier.builder()
                .supplierId(1L)
                .name("테스트 공급업체")
                .businessRegistrationNumber("1234567890")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress(null)
                .ceoName("김수용")
                .managerName("김매니저")
                .managerContact("01012345678")
                .build();

        when(supplierRepository.save(any(Supplier.class))).thenReturn(expectedSupplier);

        // when
        Supplier result = supplierService.save(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("테스트 공급업체");
        assertThat(result.getBusinessRegistrationNumber()).isEqualTo("1234567890");
        assertThat(result.getPostcode()).isEqualTo("12345");
        assertThat(result.getBaseAddress()).isEqualTo("서울시 어딘가");
        assertThat(result.getDetailAddress()).isNull();
        assertThat(result.getCeoName()).isEqualTo("김수용");
        assertThat(result.getManagerName()).isEqualTo("김매니저");
        assertThat(result.getManagerContact()).isEqualTo("01012345678");
        assertThat(result.isActive()).isTrue();

        verify(supplierRepository).save(any(Supplier.class));
    }

    @DisplayName("상세주소가 빈 문자열인 경우에도 공급업체 저장을 성공한다")
    @Test
    void saveWithEmptyDetailAddress() {
        // given
        CreateSupplierRequest request = new CreateSupplierRequest(
                "테스트 공급업체",
                "1234567890",
                "12345",
                "서울시 어딘가",
                "",
                "김수용",
                "김매니저",
                "01012345678"
        );

        Supplier expectedSupplier = Supplier.builder()
                .supplierId(1L)
                .name("테스트 공급업체")
                .businessRegistrationNumber("1234567890")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("")
                .ceoName("김수용")
                .managerName("김매니저")
                .managerContact("01012345678")
                .build();

        when(supplierRepository.save(any(Supplier.class))).thenReturn(expectedSupplier);

        // when
        Supplier result = supplierService.save(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("테스트 공급업체");
        assertThat(result.getBusinessRegistrationNumber()).isEqualTo("1234567890");
        assertThat(result.getPostcode()).isEqualTo("12345");
        assertThat(result.getBaseAddress()).isEqualTo("서울시 어딘가");
        assertThat(result.getDetailAddress()).isEmpty();
        assertThat(result.getCeoName()).isEqualTo("김수용");
        assertThat(result.getManagerName()).isEqualTo("김매니저");
        assertThat(result.getManagerContact()).isEqualTo("01012345678");
        assertThat(result.isActive()).isTrue();

        verify(supplierRepository).save(any(Supplier.class));
    }

    @DisplayName("ID로 공급업체 조회를 성공하면 해당 공급업체 정보를 반환한다")
    @Test
    void findByIdWithSuccess() {
        // given
        Long supplierId = 1L;
        Supplier expectedSupplier = Supplier.builder()
                .supplierId(supplierId)
                .name("테스트 공급업체")
                .businessRegistrationNumber("1234567890")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("상세주소")
                .ceoName("김수용")
                .managerName("김매니저")
                .managerContact("01012345678")
                .build();

        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(expectedSupplier));

        // when
        Supplier result = supplierService.findById(supplierId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getSupplierId()).isEqualTo(supplierId);
        assertThat(result.getName()).isEqualTo("테스트 공급업체");
        assertThat(result.getBusinessRegistrationNumber()).isEqualTo("1234567890");
        assertThat(result.getPostcode()).isEqualTo("12345");
        assertThat(result.getBaseAddress()).isEqualTo("서울시 어딘가");
        assertThat(result.getDetailAddress()).isEqualTo("상세주소");
        assertThat(result.getCeoName()).isEqualTo("김수용");
        assertThat(result.getManagerName()).isEqualTo("김매니저");
        assertThat(result.getManagerContact()).isEqualTo("01012345678");

        verify(supplierRepository).findById(supplierId);
    }

    @DisplayName("존재하지 않는 ID로 공급업체 조회 시 예외가 발생한다")
    @Test
    void findByIdWithNotFound() {
        // given
        Long supplierId = 999L;
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> supplierService.findById(supplierId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);

        verify(supplierRepository).findById(supplierId);
    }

    @DisplayName("null ID로 공급업체 조회 시 예외가 발생한다")
    @Test
    void findByIdWithNullId() {
        // when & then
        assertThatThrownBy(() -> supplierService.findById(null))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.INVALID_INPUT);
    }

    @DisplayName("모든 공급업체 조회를 성공하면 공급업체 목록을 반환한다")
    @Test
    void findAllWithSuccess() {
        // given
        Supplier supplier1 = Supplier.builder()
                .supplierId(1L)
                .name("공급업체1")
                .businessRegistrationNumber("1234567890")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("1호")
                .ceoName("대표1")
                .managerName("관리자1")
                .managerContact("01011111111")
                .build();

        Supplier supplier2 = Supplier.builder()
                .supplierId(2L)
                .name("공급업체2")
                .businessRegistrationNumber("0987654321")
                .postcode("54321")
                .baseAddress("서울시 어딘가")
                .detailAddress("2호")
                .ceoName("대표2")
                .managerName("관리자2")
                .managerContact("01022222222")
                .build();

        List<Supplier> expectedSuppliers = List.of(supplier1, supplier2);
        when(supplierRepository.findAll()).thenReturn(expectedSuppliers);

        // when
        List<Supplier> result = supplierService.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("공급업체1");
        assertThat(result.get(1).getName()).isEqualTo("공급업체2");

        verify(supplierRepository).findAll();
    }

    @DisplayName("공급업체 정보 수정을 성공하면 수정된 공급업체 정보를 반환한다")
    @Test
    void updateWithSuccess() {
        // given
        Long supplierId = 1L;
        UpdateSupplierRequest updateRequest = new UpdateSupplierRequest(
                "54321",
                "서울시 어딘가",
                "수정상세",
                "수정대표",
                "수정매니저",
                "01098765432"
        );

        Supplier existingSupplier = Supplier.builder()
                .supplierId(supplierId)
                .name("기존 공급업체")
                .businessRegistrationNumber("1234567890")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("기존상세")
                .ceoName("기존대표")
                .managerName("기존매니저")
                .managerContact("01012345678")
                .build();

        Supplier updatedSupplier = Supplier.builder()
                .supplierId(supplierId)
                .name("기존 공급업체") // 업데이트되지 않음
                .businessRegistrationNumber("1234567890") // 업데이트되지 않음
                .postcode("54321")
                .baseAddress("서울시 어딘가")
                .detailAddress("수정상세")
                .ceoName("수정대표")
                .managerName("수정매니저")
                .managerContact("01098765432")
                .build();

        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(existingSupplier));
        when(supplierRepository.save(any(Supplier.class))).thenReturn(updatedSupplier);

        // when
        Supplier result = supplierService.update(supplierId, updateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getSupplierId()).isEqualTo(supplierId);
        assertThat(result.getName()).isEqualTo("기존 공급업체"); // 업데이트되지 않음
        assertThat(result.getBusinessRegistrationNumber()).isEqualTo("1234567890"); // 업데이트되지 않음
        assertThat(result.getPostcode()).isEqualTo("54321");
        assertThat(result.getBaseAddress()).isEqualTo("서울시 어딘가");
        assertThat(result.getDetailAddress()).isEqualTo("수정상세");
        assertThat(result.getCeoName()).isEqualTo("수정대표");
        assertThat(result.getManagerName()).isEqualTo("수정매니저");
        assertThat(result.getManagerContact()).isEqualTo("01098765432");

        verify(supplierRepository).findById(supplierId);
        verify(supplierRepository).save(any(Supplier.class));
    }

    @DisplayName("부분 수정을 성공하면 기존 값과 수정된 값이 함께 반환된다")
    @Test
    void updateWithPartialSuccess() {
        // given
        Long supplierId = 1L;
        UpdateSupplierRequest updateRequest = new UpdateSupplierRequest(
                null, // postcode는 수정하지 않음
                null, // baseAddress는 수정하지 않음
                "수정상세",
                null, // ceoName은 수정하지 않음
                null, // managerName은 수정하지 않음
                null  // managerContact는 수정하지 않음
        );

        Supplier existingSupplier = Supplier.builder()
                .supplierId(supplierId)
                .name("기존 공급업체")
                .businessRegistrationNumber("1234567890")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("기존상세")
                .ceoName("기존대표")
                .managerName("기존매니저")
                .managerContact("01012345678")
                .build();

        Supplier updatedSupplier = Supplier.builder()
                .supplierId(supplierId)
                .name("기존 공급업체") // 업데이트되지 않음
                .businessRegistrationNumber("1234567890") // 업데이트되지 않음
                .postcode("12345") // 기존 값 유지
                .baseAddress("서울시 어딘가") // 기존 값 유지
                .detailAddress("수정상세") // 수정된 값
                .ceoName("기존대표") // 기존 값 유지
                .managerName("기존매니저") // 기존 값 유지
                .managerContact("01012345678") // 기존 값 유지
                .build();

        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(existingSupplier));
        when(supplierRepository.save(any(Supplier.class))).thenReturn(updatedSupplier);

        // when
        Supplier result = supplierService.update(supplierId, updateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("기존 공급업체"); // 업데이트되지 않음
        assertThat(result.getBusinessRegistrationNumber()).isEqualTo("1234567890");
        assertThat(result.getPostcode()).isEqualTo("12345");
        assertThat(result.getBaseAddress()).isEqualTo("서울시 어딘가");
        assertThat(result.getDetailAddress()).isEqualTo("수정상세");
        assertThat(result.getCeoName()).isEqualTo("기존대표");
        assertThat(result.getManagerName()).isEqualTo("기존매니저");
        assertThat(result.getManagerContact()).isEqualTo("01012345678");

        verify(supplierRepository).findById(supplierId);
        verify(supplierRepository).save(any(Supplier.class));
    }

    @DisplayName("존재하지 않는 공급업체 수정 시 예외가 발생한다")
    @Test
    void updateWithNotFound() {
        // given
        Long supplierId = 999L;
        UpdateSupplierRequest updateRequest = new UpdateSupplierRequest(
                "54321",
                "서울시 어딘가",
                "수정상세",
                "수정대표",
                "수정매니저",
                "01098765432"
        );

        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> supplierService.update(supplierId, updateRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);

        verify(supplierRepository).findById(supplierId);
    }

    @DisplayName("null ID로 공급업체 수정 시 예외가 발생한다")
    @Test
    void updateWithNullId() {
        // given
        UpdateSupplierRequest updateRequest = new UpdateSupplierRequest(
                "54321",
                "서울시 어딘가",
                "수정상세",
                "수정대표",
                "수정매니저",
                "01098765432"
        );

        // when & then
        assertThatThrownBy(() -> supplierService.update(null, updateRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.INVALID_INPUT);
    }

    @DisplayName("공급업체 삭제를 성공한다")
    @Test
    void deleteByIdWithSuccess() {
        // given
        Long supplierId = 1L;
        Supplier existingSupplier = Supplier.builder()
                .supplierId(supplierId)
                .name("삭제할 공급업체")
                .businessRegistrationNumber("1234567890")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("상세주소")
                .ceoName("대표")
                .managerName("매니저")
                .managerContact("01012345678")
                .build();

        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(existingSupplier));

        // when
        supplierService.deleteById(supplierId);

        // then
        verify(supplierRepository).findById(supplierId);
        verify(supplierRepository).deleteById(supplierId);
    }

    @DisplayName("존재하지 않는 공급업체 삭제 시 예외가 발생한다")
    @Test
    void deleteByIdWithNotFound() {
        // given
        Long supplierId = 999L;
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> supplierService.deleteById(supplierId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);

        verify(supplierRepository).findById(supplierId);
    }

    @DisplayName("null ID로 공급업체 삭제 시 예외가 발생한다")
    @Test
    void deleteByIdWithNullId() {
        // when & then
        assertThatThrownBy(() -> supplierService.deleteById(null))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.INVALID_INPUT);
    }
}
