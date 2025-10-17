package inventory.supplier.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.supplier.service.request.CreateSupplierRequest;
import inventory.supplier.service.request.UpdateSupplierRequest;
import inventory.supplier.service.response.SupplierResponse;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class SupplierServiceTest {

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
                "01012345678",
                "test@test.com"
        );

        // when
        SupplierResponse result = supplierService.save(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("테스트 공급업체");
        assertThat(result.businessRegistrationNumber()).isEqualTo("1234567890");
        assertThat(result.postcode()).isEqualTo("12345");
        assertThat(result.baseAddress()).isEqualTo("서울시 어딘가");
        assertThat(result.detailAddress()).isEqualTo("상세주소");
        assertThat(result.ceoName()).isEqualTo("김수용");
        assertThat(result.managerName()).isEqualTo("김매니저");
        assertThat(result.managerContact()).isEqualTo("01012345678");
        assertThat(result.active()).isTrue();
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
                "01012345678",
                "test@test.com"
        );

        // when
        SupplierResponse result = supplierService.save(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("테스트 공급업체");
        assertThat(result.businessRegistrationNumber()).isEqualTo("1234567890");
        assertThat(result.postcode()).isEqualTo("12345");
        assertThat(result.baseAddress()).isEqualTo("서울시 어딘가");
        assertThat(result.detailAddress()).isNull();
        assertThat(result.ceoName()).isEqualTo("김수용");
        assertThat(result.managerName()).isEqualTo("김매니저");
        assertThat(result.managerContact()).isEqualTo("01012345678");
        assertThat(result.active()).isTrue();
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
                "01012345678",
                "test@test.com"
        );

        // when
        SupplierResponse result = supplierService.save(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("테스트 공급업체");
        assertThat(result.businessRegistrationNumber()).isEqualTo("1234567890");
        assertThat(result.postcode()).isEqualTo("12345");
        assertThat(result.baseAddress()).isEqualTo("서울시 어딘가");
        assertThat(result.detailAddress()).isEqualTo("");
        assertThat(result.ceoName()).isEqualTo("김수용");
        assertThat(result.managerName()).isEqualTo("김매니저");
        assertThat(result.managerContact()).isEqualTo("01012345678");
        assertThat(result.active()).isTrue();
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
                "01012345678",
                "test@test.com"
        );
        SupplierResponse savedSupplier = supplierService.save(request);
        Long supplierId = savedSupplier.id();

        // when
        SupplierResponse result = supplierService.findById(supplierId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(supplierId);
        assertThat(result.name()).isEqualTo("조회 테스트 공급업체");
        assertThat(result.businessRegistrationNumber()).isEqualTo("1234567893");
        assertThat(result.postcode()).isEqualTo("12345");
        assertThat(result.baseAddress()).isEqualTo("서울시 어딘가");
        assertThat(result.detailAddress()).isEqualTo("상세주소");
        assertThat(result.ceoName()).isEqualTo("김수용");
        assertThat(result.managerName()).isEqualTo("김매니저");
        assertThat(result.managerContact()).isEqualTo("01012345678");
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

    @DisplayName("공급업체 목록 조회를 성공하면 페이징 결과를 반환한다")
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
                "01011111111",
                "test1@test.com"
        );

        CreateSupplierRequest request2 = new CreateSupplierRequest(
                "공급업체2",
                "0987654321",
                "54321",
                "서울시 어딘가",
                "2호",
                "대표2",
                "관리자2",
                "01022222222",
                "test2@test.com"
        );

        supplierService.save(request1);
        supplierService.save(request2);

        // when
        Page<SupplierResponse> page = supplierService.findAllWithConditions(
                null, null, null, PageRequest.of(0, 10)
        );

        // then
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(2);
        assertThat(page.getContent().stream().anyMatch(s -> s.name().equals("공급업체1"))).isTrue();
        assertThat(page.getContent().stream().anyMatch(s -> s.name().equals("공급업체2"))).isTrue();
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
                "01012345678",
                "old@test.com"
        );
        SupplierResponse savedSupplier = supplierService.save(createRequest);
        Long supplierId = savedSupplier.id();

        UpdateSupplierRequest updateRequest = new UpdateSupplierRequest(
                "54321",
                "서울시 어딘가",
                "수정상세",
                "수정대표",
                "수정매니저",
                "01098765432",
                "updated@test.com"
        );

        // when
        SupplierResponse result = supplierService.update(supplierId, updateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(supplierId);
        assertThat(result.name()).isEqualTo("기존 공급업체"); // 업데이트되지 않음
        assertThat(result.businessRegistrationNumber()).isEqualTo("1234567895"); // 업데이트되지 않음
        assertThat(result.postcode()).isEqualTo("54321");
        assertThat(result.baseAddress()).isEqualTo("서울시 어딘가");
        assertThat(result.detailAddress()).isEqualTo("수정상세");
        assertThat(result.ceoName()).isEqualTo("수정대표");
        assertThat(result.managerName()).isEqualTo("수정매니저");
        assertThat(result.managerContact()).isEqualTo("01098765432");
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
                "01012345678",
                "old@test.com"
        );
        SupplierResponse savedSupplier = supplierService.save(createRequest);
        Long supplierId = savedSupplier.id();

        UpdateSupplierRequest updateRequest = new UpdateSupplierRequest(
                "12345",
                "서울시 어딘가",
                "수정상세",
                "기존대표",
                "기존매니저",
                "01012345678",
                "old@test.com"
        );

        // when
        SupplierResponse result = supplierService.update(supplierId, updateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("기존 공급업체"); // 업데이트되지 않음
        assertThat(result.businessRegistrationNumber()).isEqualTo("1234567897");
        assertThat(result.postcode()).isEqualTo("12345");
        assertThat(result.baseAddress()).isEqualTo("서울시 어딘가");
        assertThat(result.detailAddress()).isEqualTo("수정상세");
        assertThat(result.ceoName()).isEqualTo("기존대표");
        assertThat(result.managerName()).isEqualTo("기존매니저");
        assertThat(result.managerContact()).isEqualTo("01012345678");
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
                "01098765432",
                "update@test.com"
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
                "01098765432",
                "update@test.com"
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
                "01012345678",
                "delete@test.com"
        );
        SupplierResponse savedSupplier = supplierService.save(request);
        Long supplierId = savedSupplier.id();

        // when & then
        supplierService.deleteById(supplierId);
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
