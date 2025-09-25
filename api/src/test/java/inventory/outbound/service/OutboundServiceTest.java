package inventory.outbound.service;

import inventory.common.exception.CustomException;
import inventory.outbound.domain.Outbound;
import inventory.outbound.domain.OutboundProduct;
import inventory.outbound.domain.enums.OutboundStatus;
import inventory.outbound.repository.OutboundProductRepository;
import inventory.outbound.repository.OutboundRepository;
import inventory.outbound.service.request.CreateOutboundRequest;
import inventory.outbound.service.request.OutboundProductRequest;
import inventory.outbound.service.response.OutboundResponse;
import inventory.product.domain.Product;
import inventory.product.repository.ProductRepository;
import inventory.supplier.domain.Supplier;
import inventory.supplier.repository.SupplierRepository;
import inventory.warehouse.domain.Warehouse;
import inventory.warehouse.domain.WarehouseStock;
import inventory.warehouse.repository.WarehouseRepository;
import inventory.warehouse.repository.WarehouseStockRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
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
class OutboundServiceTest {

    @Autowired
    private OutboundService outboundService;

    @Autowired
    private OutboundRepository outboundRepository;

    @Autowired
    private OutboundProductRepository outboundProductRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private WarehouseStockRepository warehouseStockRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    private Warehouse testWarehouse;
    private Supplier testSupplier;
    private Product testProduct1;
    private Product testProduct2;
    private WarehouseStock testStock1;
    private WarehouseStock testStock2;

    @BeforeEach
    void setUp() {
        // 테스트용 창고 생성
        testWarehouse = Warehouse.builder()
                .name("테스트 창고")
                .postcode("12345")
                .baseAddress("서울시 강남구")
                .detailAddress("테헤란로 123")
                .managerName("김창고")
                .managerContact("010-1234-5678")
                .build();
        testWarehouse = warehouseRepository.save(testWarehouse);

        // 테스트용 공급업체 생성
        testSupplier = Supplier.builder()
                .name("테스트 공급업체")
                .businessRegistrationNumber("1234567890")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("상세주소")
                .ceoName("김대표")
                .managerName("김매니저")
                .managerContact("010-9876-5432")
                .build();
        testSupplier = supplierRepository.save(testSupplier);

        // 테스트용 상품 생성
        testProduct1 = Product.builder()
                .supplierId(testSupplier.getSupplierId())
                .productName("테스트 상품1")
                .productCode("TEST001")
                .unit("개")
                .build();
        testProduct1 = productRepository.save(testProduct1);

        testProduct2 = Product.builder()
                .supplierId(testSupplier.getSupplierId())
                .productName("테스트 상품2")
                .productCode("TEST002")
                .unit("개")
                .build();
        testProduct2 = productRepository.save(testProduct2);

        // 테스트용 재고 생성
        testStock1 = WarehouseStock.builder()
                .warehouseId(testWarehouse.getWarehouseId())
                .productId(testProduct1.getProductId())
                .quantity(100)
                .safetyStock(10)
                .build();
        testStock1 = warehouseStockRepository.save(testStock1);

        testStock2 = WarehouseStock.builder()
                .warehouseId(testWarehouse.getWarehouseId())
                .productId(testProduct2.getProductId())
                .quantity(50)
                .safetyStock(5)
                .build();
        testStock2 = warehouseStockRepository.save(testStock2);
    }

    @DisplayName("출고 등록을 성공하면 출고 정보와 예약 재고가 생성된다")
    @Test
    void createOutboundWithSuccess() {
        // given
        CreateOutboundRequest request = new CreateOutboundRequest(
                testWarehouse.getWarehouseId(),
                LocalDate.now().plusDays(1),
                "김수령인",
                "01011112222",
                "12345",
                "서울시 강남구",
                "테헤란로 456",
                "문 앞에 놓아주세요",
                List.of(
                        new OutboundProductRequest(testProduct1.getProductId(), 10),
                        new OutboundProductRequest(testProduct2.getProductId(), 5)
                )
        );

        // when
        OutboundResponse response = outboundService.createOutbound(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.orderNumber()).startsWith("OB");
        assertThat(response.warehouseId()).isEqualTo(testWarehouse.getWarehouseId());
        assertThat(response.recipientName()).isEqualTo("김수령인");
        assertThat(response.status()).isEqualTo(OutboundStatus.ORDERED);
        assertThat(response.products()).hasSize(2);

        // 출고 상품이 저장되었는지 확인
        List<OutboundProduct> savedProducts = outboundProductRepository.findByOutboundId(response.outboundId());
        assertThat(savedProducts).hasSize(2);

        // 예약 재고가 생성되었는지 확인
        WarehouseStock updatedStock1 = warehouseStockRepository
                .findByWarehouseIdAndProductId(testWarehouse.getWarehouseId(), testProduct1.getProductId())
                .orElseThrow();
        assertThat(updatedStock1.getReservedQuantity()).isEqualTo(10);
        assertThat(updatedStock1.getAvailableQuantity()).isEqualTo(90); // 100 - 10

        WarehouseStock updatedStock2 = warehouseStockRepository
                .findByWarehouseIdAndProductId(testWarehouse.getWarehouseId(), testProduct2.getProductId())
                .orElseThrow();
        assertThat(updatedStock2.getReservedQuantity()).isEqualTo(5);
        assertThat(updatedStock2.getAvailableQuantity()).isEqualTo(45); // 50 - 5
    }

    @DisplayName("재고가 부족하면 출고 등록이 실패한다")
    @Test
    void createOutboundWithInsufficientStock() {
        // given
        CreateOutboundRequest request = new CreateOutboundRequest(
                testWarehouse.getWarehouseId(),
                LocalDate.now().plusDays(1),
                "김수령인",
                "01011112222",
                "12345",
                "서울시 강남구",
                "테헤란로 456",
                "문 앞에 놓아주세요",
                List.of(
                        new OutboundProductRequest(testProduct1.getProductId(), 150) // 재고 100개보다 많음
                )
        );

        // when & then
        assertThatThrownBy(() -> outboundService.createOutbound(request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("재고가 부족합니다");
    }

    @DisplayName("존재하지 않는 상품으로 출고 등록하면 실패한다")
    @Test
    void createOutboundWithNonExistentProduct() {
        // given
        CreateOutboundRequest request = new CreateOutboundRequest(
                testWarehouse.getWarehouseId(),
                LocalDate.now().plusDays(1),
                "김수령인",
                "010-1111-2222",
                "12345",
                "서울시 강남구",
                "테헤란로 456",
                "문 앞에 놓아주세요",
                List.of(
                        new OutboundProductRequest(999L, 10) // 존재하지 않는 상품 ID
                )
        );

        // when & then
        assertThatThrownBy(() -> outboundService.createOutbound(request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("상품을 찾을 수 없습니다");
    }

    @DisplayName("출고 상태를 피킹 중으로 변경할 수 있다")
    @Test
    void startPickingWithSuccess() {
        // given
        Outbound outbound = createTestOutbound();
        assertThat(outbound.getOutboundStatus()).isEqualTo(OutboundStatus.ORDERED);

        // when
        outboundService.startPicking(outbound.getOutboundId());

        // then
        Outbound updatedOutbound = outboundRepository.findById(outbound.getOutboundId()).orElseThrow();
        assertThat(updatedOutbound.getOutboundStatus()).isEqualTo(OutboundStatus.PICKING);
    }

    @DisplayName("출고 완료 시 예약 재고가 실제 재고에서 차감된다")
    @Test
    void completeOutboundWithSuccess() {
        // given
        Outbound outbound = createTestOutbound();
        outboundService.startPicking(outbound.getOutboundId());

        int initialQuantity = testStock1.getQuantity();
        int initialReservedQuantity = testStock1.getReservedQuantity();

        // when
        outboundService.completeOutbound(outbound.getOutboundId());

        // then
        Outbound completedOutbound = outboundRepository.findById(outbound.getOutboundId()).orElseThrow();
        assertThat(completedOutbound.getOutboundStatus()).isEqualTo(OutboundStatus.SHIPPED);
        assertThat(completedOutbound.getShippedDate()).isEqualTo(LocalDate.now());

        // 예약 재고가 실제 재고에서 차감되었는지 확인
        WarehouseStock updatedStock1 = warehouseStockRepository
                .findByWarehouseIdAndProductId(testWarehouse.getWarehouseId(), testProduct1.getProductId())
                .orElseThrow();
        assertThat(updatedStock1.getQuantity()).isEqualTo(initialQuantity - 10); // 실제 재고 차감
        assertThat(updatedStock1.getReservedQuantity()).isEqualTo(initialReservedQuantity - 10); // 예약 해제
    }

    @DisplayName("출고 취소 시 예약 재고가 해제된다")
    @Test
    void cancelOutboundWithSuccess() {
        // given
        Outbound outbound = createTestOutbound();
        int initialReservedQuantity = testStock1.getReservedQuantity();

        // when
        outboundService.cancelOutbound(outbound.getOutboundId());

        // then
        Outbound canceledOutbound = outboundRepository.findById(outbound.getOutboundId()).orElseThrow();
        assertThat(canceledOutbound.getOutboundStatus()).isEqualTo(OutboundStatus.CANCELED);

        // 예약 재고가 해제되었는지 확인
        WarehouseStock updatedStock1 = warehouseStockRepository
                .findByWarehouseIdAndProductId(testWarehouse.getWarehouseId(), testProduct1.getProductId())
                .orElseThrow();
        assertThat(updatedStock1.getReservedQuantity()).isEqualTo(initialReservedQuantity - 10); // 예약 해제
        assertThat(updatedStock1.getQuantity()).isEqualTo(100); // 실제 재고는 그대로
    }

    @DisplayName("출고 완료 후에는 취소할 수 없다")
    @Test
    void cannotCancelCompletedOutbound() {
        // given
        Outbound outbound = createTestOutbound();
        outboundService.startPicking(outbound.getOutboundId());
        outboundService.completeOutbound(outbound.getOutboundId());

        // when & then
        assertThatThrownBy(() -> outboundService.cancelOutbound(outbound.getOutboundId()))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("취소할 수 없는 상태입니다");
    }

    @DisplayName("출고 완료 후에는 상태 변경이 불가능하다")
    @Test
    void cannotChangeStatusAfterCompletion() {
        // given
        Outbound outbound = createTestOutbound();
        outboundService.startPicking(outbound.getOutboundId());
        outboundService.completeOutbound(outbound.getOutboundId());

        // when & then
        assertThatThrownBy(() -> outboundService.startPicking(outbound.getOutboundId()))
                .isInstanceOf(CustomException.class);
    }

    private Outbound createTestOutbound() {
        CreateOutboundRequest request = new CreateOutboundRequest(
                testWarehouse.getWarehouseId(),
                LocalDate.now().plusDays(1),
                "김수령인",
                "01011112222",
                "12345",
                "서울시 강남구",
                "테헤란로 456",
                "문 앞에 놓아주세요",
                List.of(
                        new OutboundProductRequest(testProduct1.getProductId(), 10),
                        new OutboundProductRequest(testProduct2.getProductId(), 5)
                )
        );

        OutboundResponse response = outboundService.createOutbound(request);
        return outboundRepository.findById(response.outboundId()).orElseThrow();
    }
}
