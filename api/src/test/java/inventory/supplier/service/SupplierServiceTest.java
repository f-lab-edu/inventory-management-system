package inventory.supplier.service;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.supplier.controller.request.CreateSupplierRequest;
import inventory.supplier.controller.request.UpdateSupplierRequest;
import inventory.supplier.domain.Supplier;
import inventory.supplier.repository.SupplierRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class SupplierServiceTest {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
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

        // when
        Supplier result = supplierService.save(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getSupplierId()).isNotNull();
        assertThat(result.getName()).isEqualTo("테스트 공급업체");
        assertThat(result.getBusinessRegistrationNumber()).isEqualTo("1234567890");
        assertThat(result.getPostcode()).isEqualTo("12345");
        assertThat(result.getBaseAddress()).isEqualTo("서울시 어딘가");
        assertThat(result.getDetailAddress()).isEqualTo("상세주소");
        assertThat(result.getCeoName()).isEqualTo("김수용");
        assertThat(result.getManagerName()).isEqualTo("김매니저");
        assertThat(result.getManagerContact()).isEqualTo("01012345678");
        assertThat(result.isActive()).isTrue();

        // 데이터베이스에서 실제로 저장되었는지 확인
        Supplier savedSupplier = supplierRepository.findById(result.getSupplierId()).orElse(null);
        assertThat(savedSupplier).isNotNull();
        assertThat(savedSupplier.getName()).isEqualTo("테스트 공급업체");
    }

    @DisplayName("상세주소가 null인 경우에도 공급업체 저장을 성공한다")
    @Test
    void saveWithNullDetailAddress() {
        // given
        CreateSupplierRequest request = new CreateSupplierRequest(
                "테스트 공급업체2",
                "1234567891",
                "12345",
                "서울시 어딘가",
                null,
                "김수용",
                "김매니저",
                "01012345678"
        );

        // when
        Supplier result = supplierService.save(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getSupplierId()).isNotNull();
        assertThat(result.getName()).isEqualTo("테스트 공급업체2");
        assertThat(result.getBusinessRegistrationNumber()).isEqualTo("1234567891");
        assertThat(result.getPostcode()).isEqualTo("12345");
        assertThat(result.getBaseAddress()).isEqualTo("서울시 어딘가");
        assertThat(result.getDetailAddress()).isNull();
        assertThat(result.getCeoName()).isEqualTo("김수용");
        assertThat(result.getManagerName()).isEqualTo("김매니저");
        assertThat(result.getManagerContact()).isEqualTo("01012345678");
        assertThat(result.isActive()).isTrue();

        // 데이터베이스에서 실제로 저장되었는지 확인
        Supplier savedSupplier = supplierRepository.findById(result.getSupplierId()).orElse(null);
        assertThat(savedSupplier).isNotNull();
        assertThat(savedSupplier.getDetailAddress()).isNull();
    }

    @DisplayName("상세주소가 빈 문자열인 경우에도 공급업체 저장을 성공한다")
    @Test
    void saveWithEmptyDetailAddress() {
        // given
        CreateSupplierRequest request = new CreateSupplierRequest(
                "테스트 공급업체3",
                "1234567892",
                "12345",
                "서울시 어딘가",
                "",
                "김수용",
                "김매니저",
                "01012345678"
        );

        // when
        Supplier result = supplierService.save(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getSupplierId()).isNotNull();
        assertThat(result.getName()).isEqualTo("테스트 공급업체3");
        assertThat(result.getBusinessRegistrationNumber()).isEqualTo("1234567892");
        assertThat(result.getPostcode()).isEqualTo("12345");
        assertThat(result.getBaseAddress()).isEqualTo("서울시 어딘가");
        assertThat(result.getDetailAddress()).isEmpty();
        assertThat(result.getCeoName()).isEqualTo("김수용");
        assertThat(result.getManagerName()).isEqualTo("김매니저");
        assertThat(result.getManagerContact()).isEqualTo("01012345678");
        assertThat(result.isActive()).isTrue();

        // 데이터베이스에서 실제로 저장되었는지 확인
        Supplier savedSupplier = supplierRepository.findById(result.getSupplierId()).orElse(null);
        assertThat(savedSupplier).isNotNull();
        assertThat(savedSupplier.getDetailAddress()).isEmpty();
    }

    @DisplayName("ID로 공급업체 조회를 성공하면 해당 공급업체 정보를 반환한다")
    @Test
    void findByIdWithSuccess() {
        // given
        CreateSupplierRequest request = new CreateSupplierRequest(
                "조회 테스트 공급업체",
                "1234567893",
                "12345",
                "서울시 어딘가",
                "상세주소",
                "김수용",
                "김매니저",
                "01012345678"
        );
        Supplier savedSupplier = supplierService.save(request);
        Long supplierId = savedSupplier.getSupplierId();

        // when
        Supplier result = supplierService.findById(supplierId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getSupplierId()).isEqualTo(supplierId);
        assertThat(result.getName()).isEqualTo("조회 테스트 공급업체");
        assertThat(result.getBusinessRegistrationNumber()).isEqualTo("1234567893");
        assertThat(result.getPostcode()).isEqualTo("12345");
        assertThat(result.getBaseAddress()).isEqualTo("서울시 어딘가");
        assertThat(result.getDetailAddress()).isEqualTo("상세주소");
        assertThat(result.getCeoName()).isEqualTo("김수용");
        assertThat(result.getManagerName()).isEqualTo("김매니저");
        assertThat(result.getManagerContact()).isEqualTo("01012345678");
    }

    @DisplayName("존재하지 않는 ID로 공급업체 조회 시 예외가 발생한다")
    @Test
    void findByIdWithNotFound() {
        // given
        Long supplierId = 999L;

        // when & then
        assertThatThrownBy(() -> supplierService.findById(supplierId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);
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
        CreateSupplierRequest request1 = new CreateSupplierRequest(
                "공급업체1",
                "1234567894",
                "12345",
                "서울시 어딘가",
                "1호",
                "대표1",
                "관리자1",
                "01011111111"
        );

        CreateSupplierRequest request2 = new CreateSupplierRequest(
                "공급업체2",
                "0987654321",
                "54321",
                "서울시 어딘가",
                "2호",
                "대표2",
                "관리자2",
                "01022222222"
        );

        supplierService.save(request1);
        supplierService.save(request2);

        // when
        List<Supplier> result = supplierService.findAll();

        // then
        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
        assertThat(result.stream().anyMatch(s -> s.getName().equals("공급업체1"))).isTrue();
        assertThat(result.stream().anyMatch(s -> s.getName().equals("공급업체2"))).isTrue();
    }

    @DisplayName("공급업체 정보 수정을 성공하면 수정된 공급업체 정보를 반환한다")
    @Test
    void updateWithSuccess() {
        // given
        CreateSupplierRequest createRequest = new CreateSupplierRequest(
                "기존 공급업체",
                "1234567895",
                "12345",
                "서울시 어딘가",
                "기존상세",
                "기존대표",
                "기존매니저",
                "01012345678"
        );
        Supplier savedSupplier = supplierService.save(createRequest);
        Long supplierId = savedSupplier.getSupplierId();

        UpdateSupplierRequest updateRequest = new UpdateSupplierRequest(
                "54321",
                "서울시 어딘가",
                "수정상세",
                "수정대표",
                "수정매니저",
                "01098765432"
        );

        // when
        Supplier result = supplierService.update(supplierId, updateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getSupplierId()).isEqualTo(supplierId);
        assertThat(result.getName()).isEqualTo("기존 공급업체"); // 업데이트되지 않음
        assertThat(result.getBusinessRegistrationNumber()).isEqualTo("1234567895"); // 업데이트되지 않음
        assertThat(result.getPostcode()).isEqualTo("54321");
        assertThat(result.getBaseAddress()).isEqualTo("서울시 어딘가");
        assertThat(result.getDetailAddress()).isEqualTo("수정상세");
        assertThat(result.getCeoName()).isEqualTo("수정대표");
        assertThat(result.getManagerName()).isEqualTo("수정매니저");
        assertThat(result.getManagerContact()).isEqualTo("01098765432");

        // 데이터베이스에서 실제로 수정되었는지 확인
        Supplier updatedSupplier = supplierRepository.findById(supplierId).orElse(null);
        assertThat(updatedSupplier).isNotNull();
        assertThat(updatedSupplier.getPostcode()).isEqualTo("54321");
        assertThat(updatedSupplier.getDetailAddress()).isEqualTo("수정상세");
    }

    @DisplayName("부분 수정을 성공하면 기존 값과 수정된 값이 함께 반환된다")
    @Test
    void updateWithPartialSuccess() {
        // given
        CreateSupplierRequest createRequest = new CreateSupplierRequest(
                "기존 공급업체",
                "1234567897",
                "12345",
                "서울시 어딘가",
                "기존상세",
                "기존대표",
                "기존매니저",
                "01012345678"
        );
        Supplier savedSupplier = supplierService.save(createRequest);
        Long supplierId = savedSupplier.getSupplierId();

        UpdateSupplierRequest updateRequest = new UpdateSupplierRequest(
                "12345",
                "서울시 어딘가",
                "수정상세",
                "기존대표",
                "기존매니저",
                "01012345678"
        );

        // when
        Supplier result = supplierService.update(supplierId, updateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("기존 공급업체"); // 업데이트되지 않음
        assertThat(result.getBusinessRegistrationNumber()).isEqualTo("1234567897");
        assertThat(result.getPostcode()).isEqualTo("12345");
        assertThat(result.getBaseAddress()).isEqualTo("서울시 어딘가");
        assertThat(result.getDetailAddress()).isEqualTo("수정상세");
        assertThat(result.getCeoName()).isEqualTo("기존대표");
        assertThat(result.getManagerName()).isEqualTo("기존매니저");
        assertThat(result.getManagerContact()).isEqualTo("01012345678");

        Supplier updatedSupplier = supplierRepository.findById(supplierId).orElse(null);
        assertThat(updatedSupplier).isNotNull();
        assertThat(updatedSupplier.getPostcode()).isEqualTo("12345"); // 기존 값 유지
        assertThat(updatedSupplier.getDetailAddress()).isEqualTo("수정상세"); // 수정된 값
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

        // when & then
        assertThatThrownBy(() -> supplierService.update(supplierId, updateRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);
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
        CreateSupplierRequest request = new CreateSupplierRequest(
                "삭제할 공급업체",
                "1234567896",
                "12345",
                "서울시 어딘가",
                "상세주소",
                "대표",
                "매니저",
                "01012345678"
        );
        Supplier savedSupplier = supplierService.save(request);
        Long supplierId = savedSupplier.getSupplierId();

        // when
        supplierService.deleteById(supplierId);

        // then
        // 데이터베이스에서 실제로 삭제되었는지 확인
        assertThat(supplierRepository.findById(supplierId)).isEmpty();
    }

    @DisplayName("존재하지 않는 공급업체 삭제 시 예외가 발생한다")
    @Test
    void deleteByIdWithNotFound() {
        // given
        Long supplierId = 999L;

        // when & then
        assertThatThrownBy(() -> supplierService.deleteById(supplierId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);
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
