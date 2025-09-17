package inventory.inbound.service;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.inbound.service.request.CreateInboundRequest;
import inventory.inbound.service.request.InboundProductRequest;
import inventory.inbound.service.request.UpdateInboundStatusRequest;
import inventory.inbound.service.response.InboundResponse;
import inventory.inbound.domain.Inbound;
import inventory.inbound.domain.enums.InboundStatus;
import inventory.inbound.repository.InboundProductRepository;
import inventory.inbound.repository.InboundRepository;
import inventory.product.domain.Product;
import inventory.product.repository.ProductRepository;
import inventory.supplier.domain.Supplier;
import inventory.supplier.repository.SupplierRepository;
import inventory.warehouse.domain.Warehouse;
import inventory.warehouse.domain.WarehouseStock;
import inventory.warehouse.repository.WarehouseRepository;
import inventory.warehouse.repository.WarehouseStockRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class InboundServiceTest {

    @Autowired
    private InboundService inboundService;

    @Autowired
    private InboundRepository inboundRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InboundProductRepository inboundProductRepository;

    @Autowired
    private WarehouseStockRepository warehouseStockRepository;

    private Warehouse createTestWarehouse(String name) {
        Warehouse warehouse = Warehouse.builder()
                .name(name)
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("상세주소")
                .managerName("관리자")
                .managerContact("01012345678")
                .build();
        return warehouseRepository.save(warehouse);
    }

    private Supplier createTestSupplier(String name, String businessRegistrationNumber) {
        Supplier supplier = Supplier.builder()
                .name(name)
                .businessRegistrationNumber(businessRegistrationNumber)
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("상세주소")
                .ceoName("대표")
                .managerName("매니저")
                .managerContact("01012345678")
                .build();
        return supplierRepository.save(supplier);
    }

    private Product createTestProduct(Long supplierId, String productName, String productCode) {
        Product product = Product.builder()
                .supplierId(supplierId)
                .productName(productName)
                .productCode(productCode)
                .unit("개")
                .build();
        return productRepository.save(product);
    }

    @DisplayName("입고 생성을 성공하면 저장된 입고 정보를 반환한다")
    @Test
    void saveWithSuccess() {
        // given
        Warehouse testWarehouse = createTestWarehouse("테스트 창고");
        Supplier testSupplier = createTestSupplier("테스트 공급업체", "1234567890");
        Product testProduct1 = createTestProduct(testSupplier.getSupplierId(), "상품1", "PROD001");
        Product testProduct2 = createTestProduct(testSupplier.getSupplierId(), "상품2", "PROD002");

        CreateInboundRequest request = new CreateInboundRequest(
                testWarehouse.getWarehouseId(),
                testSupplier.getSupplierId(),
                LocalDate.now(),
                List.of(
                        new InboundProductRequest(testProduct1.getProductId(), 10),
                        new InboundProductRequest(testProduct2.getProductId(), 20)
                )
        );

        // when
        InboundResponse result = inboundService.save(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.warehouseId()).isEqualTo(testWarehouse.getWarehouseId());
        assertThat(result.supplierId()).isEqualTo(testSupplier.getSupplierId());
        assertThat(result.expectedDate()).isEqualTo(LocalDate.of(2024, 1, 15));
        assertThat(result.status()).isEqualTo(InboundStatus.REGISTERED);
        assertThat(result.products()).hasSize(2);
        assertThat(result.products().get(0).productId()).isEqualTo(testProduct1.getProductId());
        assertThat(result.products().get(0).quantity()).isEqualTo(10);
        assertThat(result.products().get(1).productId()).isEqualTo(testProduct2.getProductId());
        assertThat(result.products().get(1).quantity()).isEqualTo(20);

        Inbound savedInbound = inboundRepository.findById(result.id()).orElse(null);
        assertThat(savedInbound).isNotNull();
        assertThat(savedInbound.getStatus()).isEqualTo(InboundStatus.REGISTERED);
    }

    @DisplayName("존재하지 않는 창고로 입고 생성 시 예외가 발생한다")
    @Test
    void saveWithWarehouseNotFound() {
        // given
        Supplier testSupplier = createTestSupplier("테스트 공급업체", "1234567891");
        Product testProduct = createTestProduct(testSupplier.getSupplierId(), "상품1", "PROD003");

        CreateInboundRequest request = new CreateInboundRequest(
                999L, // 존재하지 않는 창고 ID
                testSupplier.getSupplierId(),
                LocalDate.now(),
                List.of(new InboundProductRequest(testProduct.getProductId(), 10))
        );

        // when & then
        assertThatThrownBy(() -> inboundService.save(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);
    }

    @DisplayName("존재하지 않는 공급업체로 입고 생성 시 예외가 발생한다")
    @Test
    void saveWithSupplierNotFound() {
        // given
        Warehouse testWarehouse = createTestWarehouse("테스트 창고2");
        Supplier testSupplier = createTestSupplier("테스트 공급업체", "1234567891");
        Product testProduct = createTestProduct(testSupplier.getSupplierId(), "상품1", "PROD004");

        CreateInboundRequest request = new CreateInboundRequest(
                testWarehouse.getWarehouseId(),
                999L, // 존재하지 않는 공급업체 ID
                LocalDate.now(),
                List.of(new InboundProductRequest(testProduct.getProductId(), 10))
        );

        // when & then
        assertThatThrownBy(() -> inboundService.save(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);
    }

    @DisplayName("존재하지 않는 상품으로 입고 생성 시 예외가 발생한다")
    @Test
    void saveWithProductNotFound() {
        // given
        Warehouse testWarehouse = createTestWarehouse("테스트 창고3");
        Supplier testSupplier = createTestSupplier("테스트 공급업체", "1234567892");

        CreateInboundRequest request = new CreateInboundRequest(
                testWarehouse.getWarehouseId(),
                testSupplier.getSupplierId(),
                LocalDate.now(),
                List.of(new InboundProductRequest(999L, 10)) // 존재하지 않는 상품 ID
        );

        // when & then
        assertThatThrownBy(() -> inboundService.save(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);
    }

    @DisplayName("입고 ID로 조회를 성공하면 해당 입고 정보를 반환한다")
    @Test
    void findByIdWithSuccess() {
        // given
        Warehouse testWarehouse = createTestWarehouse("조회 테스트 창고");
        Supplier testSupplier = createTestSupplier("조회 테스트 공급업체", "1234567893");
        Product testProduct = createTestProduct(testSupplier.getSupplierId(), "조회 테스트 상품", "PROD005");

        CreateInboundRequest request = new CreateInboundRequest(
                testWarehouse.getWarehouseId(),
                testSupplier.getSupplierId(),
                LocalDate.now(),
                List.of(new InboundProductRequest(testProduct.getProductId(), 10))
        );
        InboundResponse savedInbound = inboundService.save(request);
        Long inboundId = savedInbound.id();

        // when
        InboundResponse result = inboundService.findById(inboundId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(inboundId);
        assertThat(result.products()).hasSize(1);
        assertThat(result.products().get(0).quantity()).isEqualTo(10);
        assertThat(result.status()).isEqualTo(InboundStatus.REGISTERED);
    }

    @DisplayName("존재하지 않는 입고 ID로 조회 시 예외가 발생한다")
    @Test
    void findByIdWithNotFound() {
        // given
        Long inboundId = 999L;

        // when & then
        assertThatThrownBy(() -> inboundService.findById(inboundId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);
    }

    @DisplayName("null ID로 입고 조회 시 예외가 발생한다")
    @Test
    void findByIdWithNullId() {
        // when & then
        assertThatThrownBy(() -> inboundService.findById(null))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.INVALID_INPUT);
    }

    @DisplayName("전체 입고 조회를 성공하면 입고 목록을 반환한다")
    @Test
    void findAllWithSuccess() {
        // given
        Warehouse testWarehouse1 = createTestWarehouse("전체 조회 창고1");
        Warehouse testWarehouse2 = createTestWarehouse("전체 조회 창고2");
        Supplier testSupplier1 = createTestSupplier("전체 조회 공급업체1", "1234567894");
        Supplier testSupplier2 = createTestSupplier("전체 조회 공급업체2", "1234567895");
        Product testProduct1 = createTestProduct(testSupplier1.getSupplierId(), "전체 조회 상품1", "PROD006");
        Product testProduct2 = createTestProduct(testSupplier2.getSupplierId(), "전체 조회 상품2", "PROD007");

        CreateInboundRequest request1 = new CreateInboundRequest(
                testWarehouse1.getWarehouseId(),
                testSupplier1.getSupplierId(),
                LocalDate.now(),
                List.of(new InboundProductRequest(testProduct1.getProductId(), 10))
        );

        CreateInboundRequest request2 = new CreateInboundRequest(
                testWarehouse2.getWarehouseId(),
                testSupplier2.getSupplierId(),
                LocalDate.of(2024, 1, 16),
                List.of(new InboundProductRequest(testProduct2.getProductId(), 20))
        );

        inboundService.save(request1);
        inboundService.save(request2);

        // when
        List<Inbound> result = inboundService.findAll();

        // then
        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
        assertThat(result.stream().anyMatch(i -> i.getExpectedDate().equals(LocalDate.of(2024, 1, 15)))).isTrue();
        assertThat(result.stream().anyMatch(i -> i.getExpectedDate().equals(LocalDate.of(2024, 1, 16)))).isTrue();
    }

    @DisplayName("입고 상태 수정을 성공하면 수정된 입고 정보를 반환한다")
    @Test
    void updateStatusWithSuccess() {
        // given
        Warehouse testWarehouse = createTestWarehouse("상태 수정 테스트 창고");
        Supplier testSupplier = createTestSupplier("상태 수정 테스트 공급업체", "1234567896");
        Product testProduct = createTestProduct(testSupplier.getSupplierId(), "상태 수정 테스트 상품", "PROD008");

        CreateInboundRequest createRequest = new CreateInboundRequest(
                testWarehouse.getWarehouseId(),
                testSupplier.getSupplierId(),
                LocalDate.now(),
                List.of(new InboundProductRequest(testProduct.getProductId(), 10))
        );
        InboundResponse savedInbound = inboundService.save(createRequest);
        Long inboundId = savedInbound.id();

        UpdateInboundStatusRequest updateRequest = new UpdateInboundStatusRequest(InboundStatus.INSPECTING);

        // when
        InboundResponse result = inboundService.updateStatus(inboundId, updateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(inboundId);
        assertThat(result.status()).isEqualTo(InboundStatus.INSPECTING);
        assertThat(result.warehouseId()).isEqualTo(testWarehouse.getWarehouseId()); // 업데이트되지 않음
        assertThat(result.supplierId()).isEqualTo(testSupplier.getSupplierId()); // 업데이트되지 않음
        assertThat(result.products()).hasSize(1); // 업데이트되지 않음
        assertThat(result.products().get(0).quantity()).isEqualTo(10); // 업데이트되지 않음

        Inbound updatedInbound = inboundRepository.findById(inboundId).orElse(null);
        assertThat(updatedInbound).isNotNull();
        assertThat(updatedInbound.getStatus()).isEqualTo(InboundStatus.INSPECTING);
    }

    @DisplayName("존재하지 않는 입고 상태 수정 시 예외가 발생한다")
    @Test
    void updateStatusWithNotFound() {
        // given
        Long inboundId = 999L;
        UpdateInboundStatusRequest updateRequest = new UpdateInboundStatusRequest(InboundStatus.INSPECTING);

        // when & then
        assertThatThrownBy(() -> inboundService.updateStatus(inboundId, updateRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);
    }

    @DisplayName("null ID로 입고 상태 수정 시 예외가 발생한다")
    @Test
    void updateStatusWithNullId() {
        // given
        UpdateInboundStatusRequest updateRequest = new UpdateInboundStatusRequest(InboundStatus.INSPECTING);

        // when & then
        assertThatThrownBy(() -> inboundService.updateStatus(null, updateRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.INVALID_INPUT);
    }

    @DisplayName("입고 삭제를 성공하면 입고가 삭제된다")
    @Test
    void deleteByIdWithSuccess() {
        // given
        Warehouse testWarehouse = createTestWarehouse("삭제 테스트 창고");
        Supplier testSupplier = createTestSupplier("삭제 테스트 공급업체", "1234567897");
        Product testProduct = createTestProduct(testSupplier.getSupplierId(), "삭제 테스트 상품", "PROD009");

        CreateInboundRequest request = new CreateInboundRequest(
                testWarehouse.getWarehouseId(),
                testSupplier.getSupplierId(),
                LocalDate.now(),
                List.of(new InboundProductRequest(testProduct.getProductId(), 10))
        );
        InboundResponse savedInbound = inboundService.save(request);
        Long inboundId = savedInbound.id();

        // when
        inboundService.deleteById(inboundId);

        // then
        assertThat(inboundRepository.findById(inboundId)).isEmpty();
    }

    @DisplayName("존재하지 않는 입고 삭제 시 예외가 발생한다")
    @Test
    void deleteByIdWithNotFound() {
        // given
        Long inboundId = 999L;

        // when & then
        assertThatThrownBy(() -> inboundService.deleteById(inboundId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);
    }

    @DisplayName("null ID로 입고 삭제 시 예외가 발생한다")
    @Test
    void deleteByIdWithNullId() {
        // when & then
        assertThatThrownBy(() -> inboundService.deleteById(null))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.INVALID_INPUT);
    }

    @DisplayName("입고 완료 시 기존 상품의 재고가 증가한다")
    @Test
    void updateStatusToCompletedIncreasesExistingStock() {
        // given
        Warehouse testWarehouse = createTestWarehouse("재고 증가 테스트 창고");
        Supplier testSupplier = createTestSupplier("재고 증가 테스트 공급업체", "1234567898");
        Product testProduct = createTestProduct(testSupplier.getSupplierId(), "재고 증가 테스트 상품", "PROD010");

        // 기존 재고 생성 (수량 50)
        WarehouseStock existingStock = WarehouseStock.builder()
                .warehouseId(testWarehouse.getWarehouseId())
                .productId(testProduct.getProductId())
                .quantity(50)
                .safetyStock(10)
                .build();
        warehouseStockRepository.save(existingStock);

        CreateInboundRequest createRequest = new CreateInboundRequest(
                testWarehouse.getWarehouseId(),
                testSupplier.getSupplierId(),
                LocalDate.now(),
                List.of(new InboundProductRequest(testProduct.getProductId(), 30))
        );
        InboundResponse savedInbound = inboundService.save(createRequest);
        Long inboundId = savedInbound.id();

        // 검수 중으로 상태 변경
        UpdateInboundStatusRequest inspectingRequest = new UpdateInboundStatusRequest(InboundStatus.INSPECTING);
        inboundService.updateStatus(inboundId, inspectingRequest);

        // when - 입고 완료로 상태 변경
        UpdateInboundStatusRequest completedRequest = new UpdateInboundStatusRequest(InboundStatus.COMPLETED);
        InboundResponse result = inboundService.updateStatus(inboundId, completedRequest);

        // then
        assertThat(result.status()).isEqualTo(InboundStatus.COMPLETED);

        // 재고 확인
        WarehouseStock updatedStock = warehouseStockRepository
                .findByWarehouseIdAndProductId(testWarehouse.getWarehouseId(), testProduct.getProductId())
                .orElse(null);

        assertThat(updatedStock).isNotNull();
        assertThat(updatedStock.getQuantity()).isEqualTo(80); // 50 + 30
        assertThat(updatedStock.getSafetyStock()).isEqualTo(10); // 안전재고는 변경되지 않음
    }

    @DisplayName("입고 완료 시 새로운 상품의 재고가 생성된다")
    @Test
    void updateStatusToCompletedCreatesNewStock() {
        // given
        Warehouse testWarehouse = createTestWarehouse("신규 재고 테스트 창고");
        Supplier testSupplier = createTestSupplier("신규 재고 테스트 공급업체", "1234567899");
        Product testProduct = createTestProduct(testSupplier.getSupplierId(), "신규 재고 테스트 상품", "PROD011");

        // 기존 재고가 없는 상태에서 시작
        assertThat(warehouseStockRepository
                .findByWarehouseIdAndProductId(testWarehouse.getWarehouseId(), testProduct.getProductId()))
                .isEmpty();

        CreateInboundRequest createRequest = new CreateInboundRequest(
                testWarehouse.getWarehouseId(),
                testSupplier.getSupplierId(),
                LocalDate.now(),
                List.of(new InboundProductRequest(testProduct.getProductId(), 25))
        );
        InboundResponse savedInbound = inboundService.save(createRequest);
        Long inboundId = savedInbound.id();

        // 검수 중으로 상태 변경
        UpdateInboundStatusRequest inspectingRequest = new UpdateInboundStatusRequest(InboundStatus.INSPECTING);
        inboundService.updateStatus(inboundId, inspectingRequest);

        // when - 입고 완료로 상태 변경
        UpdateInboundStatusRequest completedRequest = new UpdateInboundStatusRequest(InboundStatus.COMPLETED);
        InboundResponse result = inboundService.updateStatus(inboundId, completedRequest);

        // then
        assertThat(result.status()).isEqualTo(InboundStatus.COMPLETED);

        // 새로운 재고 생성 확인
        WarehouseStock newStock = warehouseStockRepository
                .findByWarehouseIdAndProductId(testWarehouse.getWarehouseId(), testProduct.getProductId())
                .orElse(null);

        assertThat(newStock).isNotNull();
        assertThat(newStock.getQuantity()).isEqualTo(25); // 입고 수량과 동일
        assertThat(newStock.getSafetyStock()).isEqualTo(0); // 기본 안전재고 0
    }

    @DisplayName("입고 완료 시 여러 상품의 재고가 동시에 업데이트된다")
    @Test
    void updateStatusToCompletedUpdatesMultipleProducts() {
        // given
        Warehouse testWarehouse = createTestWarehouse("다중 재고 테스트 창고");
        Supplier testSupplier = createTestSupplier("다중 재고 테스트 공급업체", "1234567900");
        Product testProduct1 = createTestProduct(testSupplier.getSupplierId(), "다중 재고 테스트 상품1", "PROD012");
        Product testProduct2 = createTestProduct(testSupplier.getSupplierId(), "다중 재고 테스트 상품2", "PROD013");

        // 첫 번째 상품은 기존 재고 있음 (수량 100)
        WarehouseStock existingStock1 = WarehouseStock.builder()
                .warehouseId(testWarehouse.getWarehouseId())
                .productId(testProduct1.getProductId())
                .quantity(100)
                .safetyStock(20)
                .build();
        warehouseStockRepository.save(existingStock1);

        // 두 번째 상품은 기존 재고 없음
        assertThat(warehouseStockRepository
                .findByWarehouseIdAndProductId(testWarehouse.getWarehouseId(), testProduct2.getProductId()))
                .isEmpty();

        CreateInboundRequest createRequest = new CreateInboundRequest(
                testWarehouse.getWarehouseId(),
                testSupplier.getSupplierId(),
                LocalDate.now(),
                List.of(
                        new InboundProductRequest(testProduct1.getProductId(), 40), // 기존 재고 증가
                        new InboundProductRequest(testProduct2.getProductId(), 60)  // 신규 재고 생성
                )
        );
        InboundResponse savedInbound = inboundService.save(createRequest);
        Long inboundId = savedInbound.id();

        // 검수 중으로 상태 변경
        UpdateInboundStatusRequest inspectingRequest = new UpdateInboundStatusRequest(InboundStatus.INSPECTING);
        inboundService.updateStatus(inboundId, inspectingRequest);

        // when - 입고 완료로 상태 변경
        UpdateInboundStatusRequest completedRequest = new UpdateInboundStatusRequest(InboundStatus.COMPLETED);
        InboundResponse result = inboundService.updateStatus(inboundId, completedRequest);

        // then
        assertThat(result.status()).isEqualTo(InboundStatus.COMPLETED);

        // 첫 번째 상품 재고 확인 (기존 재고 증가)
        WarehouseStock updatedStock1 = warehouseStockRepository
                .findByWarehouseIdAndProductId(testWarehouse.getWarehouseId(), testProduct1.getProductId())
                .orElse(null);

        assertThat(updatedStock1).isNotNull();
        assertThat(updatedStock1.getQuantity()).isEqualTo(140); // 100 + 40
        assertThat(updatedStock1.getSafetyStock()).isEqualTo(20); // 안전재고 유지

        // 두 번째 상품 재고 확인 (신규 재고 생성)
        WarehouseStock newStock2 = warehouseStockRepository
                .findByWarehouseIdAndProductId(testWarehouse.getWarehouseId(), testProduct2.getProductId())
                .orElse(null);

        assertThat(newStock2).isNotNull();
        assertThat(newStock2.getQuantity()).isEqualTo(60); // 입고 수량과 동일
        assertThat(newStock2.getSafetyStock()).isEqualTo(0); // 기본 안전재고 0
    }

    @DisplayName("입고 거절 시 재고는 변경되지 않는다")
    @Test
    void updateStatusToRejectedDoesNotChangeStock() {
        // given
        Warehouse testWarehouse = createTestWarehouse("재고 미변경 테스트 창고");
        Supplier testSupplier = createTestSupplier("재고 미변경 테스트 공급업체", "1234567901");
        Product testProduct = createTestProduct(testSupplier.getSupplierId(), "재고 미변경 테스트 상품", "PROD014");

        // 기존 재고 생성 (수량 30)
        WarehouseStock existingStock = WarehouseStock.builder()
                .warehouseId(testWarehouse.getWarehouseId())
                .productId(testProduct.getProductId())
                .quantity(30)
                .safetyStock(5)
                .build();
        warehouseStockRepository.save(existingStock);

        CreateInboundRequest createRequest = new CreateInboundRequest(
                testWarehouse.getWarehouseId(),
                testSupplier.getSupplierId(),
                LocalDate.now(),
                List.of(new InboundProductRequest(testProduct.getProductId(), 20))
        );
        InboundResponse savedInbound = inboundService.save(createRequest);
        Long inboundId = savedInbound.id();

        // 검수 중으로 상태 변경
        UpdateInboundStatusRequest inspectingRequest = new UpdateInboundStatusRequest(InboundStatus.INSPECTING);
        inboundService.updateStatus(inboundId, inspectingRequest);

        // when - 입고 거절로 상태 변경
        UpdateInboundStatusRequest rejectedRequest = new UpdateInboundStatusRequest(InboundStatus.REJECTED);
        InboundResponse result = inboundService.updateStatus(inboundId, rejectedRequest);

        // then
        assertThat(result.status()).isEqualTo(InboundStatus.REJECTED);

        // 재고가 변경되지 않았는지 확인
        WarehouseStock unchangedStock = warehouseStockRepository
                .findByWarehouseIdAndProductId(testWarehouse.getWarehouseId(), testProduct.getProductId())
                .orElse(null);

        assertThat(unchangedStock).isNotNull();
        assertThat(unchangedStock.getQuantity()).isEqualTo(30); // 기존 재고 유지
        assertThat(unchangedStock.getSafetyStock()).isEqualTo(5); // 안전재고 유지
    }
}
