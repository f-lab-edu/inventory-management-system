package inventory.inbound.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.inbound.controller.request.CreateInboundRequest;
import inventory.inbound.controller.request.InboundProductRequest;
import inventory.inbound.controller.request.UpdateInboundStatusRequest;
import inventory.inbound.domain.Inbound;
import inventory.inbound.domain.InboundProduct;
import inventory.inbound.enums.InboundStatus;
import inventory.inbound.repository.InboundRepository;
import inventory.product.domain.Product;
import inventory.product.repository.ProductRepository;
import inventory.supplier.domain.Supplier;
import inventory.supplier.repository.SupplierRepository;
import inventory.warehouse.domain.Warehouse;
import inventory.warehouse.repository.WarehouseRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InboundServiceTest {

    @Mock
    private InboundRepository inboundRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private InboundService inboundService;

    @DisplayName("입고 생성을 성공하면 저장된 입고 정보를 반환한다")
    @Test
    void saveWithSuccess() {
        // given
        CreateInboundRequest request = new CreateInboundRequest(
                1L,
                1L,
                LocalDate.of(2024, 1, 15),
                List.of(
                        new InboundProductRequest(1L, 10),
                        new InboundProductRequest(2L, 20)
                )
        );

        Warehouse warehouse = Warehouse.builder()
                .name("테스트 창고")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("상세주소")
                .managerName("관리자")
                .managerContact("01012345678")
                .build();

        Supplier supplier = Supplier.builder()
                .name("테스트 공급업체")
                .businessRegistrationNumber("1234567890")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("상세주소")
                .ceoName("대표")
                .managerName("매니저")
                .managerContact("01012345678")
                .build();

        Product product1 = Product.builder()
                .productId(1L)
                .supplierId(1L)
                .productName("상품1")
                .productCode("PROD001")
                .unit("개")
                .thumbnailUrl("https://example.com/1.jpg")
                .active(true)
                .build();

        Product product2 = Product.builder()
                .productId(2L)
                .supplierId(1L)
                .productName("상품2")
                .productCode("PROD002")
                .unit("박스")
                .thumbnailUrl("https://example.com/2.jpg")
                .active(true)
                .build();

        List<InboundProduct> inboundProducts = List.of(
                new InboundProduct(1L, 10),
                new InboundProduct(2L, 20)
        );

        Inbound inbound = Inbound.builder()
                .inboundId(1L)
                .warehouseId(1L)
                .supplierId(1L)
                .expectedDate(LocalDate.of(2024, 1, 15))
                .products(inboundProducts)
                .status(InboundStatus.REGISTERED)
                .build();

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(inboundRepository.save(any(Inbound.class))).thenReturn(inbound);

        // when
        Inbound result = inboundService.save(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getInboundId()).isEqualTo(1L);
        assertThat(result.getWarehouseId()).isEqualTo(1L);
        assertThat(result.getSupplierId()).isEqualTo(1L);
        assertThat(result.getExpectedDate()).isEqualTo(LocalDate.of(2024, 1, 15));
        assertThat(result.getStatus()).isEqualTo(InboundStatus.REGISTERED);
        assertThat(result.getProducts()).hasSize(2);
        assertThat(result.getProducts().get(0).productId()).isEqualTo(1L);
        assertThat(result.getProducts().get(0).quantity()).isEqualTo(10);
        assertThat(result.getProducts().get(1).productId()).isEqualTo(2L);
        assertThat(result.getProducts().get(1).quantity()).isEqualTo(20);

        verify(warehouseRepository).findById(1L);
        verify(supplierRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(productRepository).findById(2L);
        verify(inboundRepository).save(any(Inbound.class));
    }

    @DisplayName("존재하지 않는 창고로 입고 생성 시 예외가 발생한다")
    @Test
    void saveWithWarehouseNotFound() {
        // given
        CreateInboundRequest request = new CreateInboundRequest(
                999L,
                1L,
                LocalDate.of(2024, 1, 15),
                List.of(new InboundProductRequest(1L, 10))
        );

        when(warehouseRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> inboundService.save(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);

        verify(warehouseRepository).findById(999L);
    }

    @DisplayName("존재하지 않는 공급업체로 입고 생성 시 예외가 발생한다")
    @Test
    void saveWithSupplierNotFound() {
        // given
        CreateInboundRequest request = new CreateInboundRequest(
                1L,
                999L,
                LocalDate.of(2024, 1, 15),
                List.of(new InboundProductRequest(1L, 10))
        );

        Warehouse warehouse = Warehouse.builder()
                .name("테스트 창고")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("상세주소")
                .managerName("관리자")
                .managerContact("01012345678")
                .build();

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(supplierRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> inboundService.save(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);

        verify(warehouseRepository).findById(1L);
        verify(supplierRepository).findById(999L);
    }

    @DisplayName("존재하지 않는 상품으로 입고 생성 시 예외가 발생한다")
    @Test
    void saveWithProductNotFound() {
        // given
        CreateInboundRequest request = new CreateInboundRequest(
                1L,
                1L,
                LocalDate.of(2024, 1, 15),
                List.of(new InboundProductRequest(999L, 10))
        );

        Warehouse warehouse = Warehouse.builder()
                .name("테스트 창고")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("상세주소")
                .managerName("관리자")
                .managerContact("01012345678")
                .build();

        Supplier supplier = Supplier.builder()
                .name("테스트 공급업체")
                .businessRegistrationNumber("1234567890")
                .postcode("12345")
                .baseAddress("서울시 어딘가")
                .detailAddress("상세주소")
                .ceoName("대표")
                .managerName("매니저")
                .managerContact("01012345678")
                .build();

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> inboundService.save(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);

        verify(warehouseRepository).findById(1L);
        verify(supplierRepository).findById(1L);
        verify(productRepository).findById(999L);
    }

    @DisplayName("입고 ID로 조회를 성공하면 해당 입고 정보를 반환한다")
    @Test
    void findByIdWithSuccess() {
        // given
        Long inboundId = 1L;
        List<InboundProduct> inboundProducts = List.of(
                new InboundProduct(1L, 10)
        );

        Inbound inbound = Inbound.builder()
                .inboundId(inboundId)
                .warehouseId(1L)
                .supplierId(1L)
                .expectedDate(LocalDate.of(2024, 1, 15))
                .products(inboundProducts)
                .status(InboundStatus.REGISTERED)
                .build();

        when(inboundRepository.findById(inboundId)).thenReturn(Optional.of(inbound));

        // when
        Inbound result = inboundService.findById(inboundId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getInboundId()).isEqualTo(inboundId);
        assertThat(result.getProducts()).hasSize(1);
        assertThat(result.getProducts().get(0).quantity()).isEqualTo(10);
        assertThat(result.getStatus()).isEqualTo(InboundStatus.REGISTERED);

        verify(inboundRepository).findById(inboundId);
    }

    @DisplayName("존재하지 않는 입고 ID로 조회 시 예외가 발생한다")
    @Test
    void findByIdWithNotFound() {
        // given
        Long inboundId = 999L;
        when(inboundRepository.findById(inboundId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> inboundService.findById(inboundId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);

        verify(inboundRepository).findById(inboundId);
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
        List<InboundProduct> products1 = List.of(
                new InboundProduct(1L, 10)
        );
        List<InboundProduct> products2 = List.of(
                new InboundProduct(2L, 20)
        );

        List<Inbound> inbounds = List.of(
                Inbound.builder().inboundId(1L).warehouseId(1L).supplierId(1L)
                        .expectedDate(LocalDate.of(2024, 1, 15)).products(products1).status(InboundStatus.REGISTERED).build(),
                Inbound.builder().inboundId(2L).warehouseId(1L).supplierId(1L)
                        .expectedDate(LocalDate.of(2024, 1, 16)).products(products2).status(InboundStatus.INSPECTING).build()
        );

        when(inboundRepository.findAll()).thenReturn(inbounds);

        // when
        List<Inbound> result = inboundService.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getInboundId()).isEqualTo(1L);
        assertThat(result.get(1).getInboundId()).isEqualTo(2L);

        verify(inboundRepository).findAll();
    }

    @DisplayName("입고 상태 수정을 성공하면 수정된 입고 정보를 반환한다")
    @Test
    void updateStatusWithSuccess() {
        // given
        Long inboundId = 1L;
        UpdateInboundStatusRequest updateRequest = new UpdateInboundStatusRequest(InboundStatus.INSPECTING);

        List<InboundProduct> inboundProducts = List.of(
                new InboundProduct(1L, 10)
        );

        Inbound existingInbound = Inbound.builder()
                .inboundId(inboundId)
                .warehouseId(1L)
                .supplierId(1L)
                .expectedDate(LocalDate.of(2024, 1, 15))
                .products(inboundProducts)
                .status(InboundStatus.REGISTERED)
                .build();

        Inbound updatedInbound = Inbound.builder()
                .inboundId(inboundId)
                .warehouseId(1L) // 업데이트되지 않음
                .supplierId(1L) // 업데이트되지 않음
                .expectedDate(LocalDate.of(2024, 1, 15)) // 업데이트되지 않음
                .products(inboundProducts) // 업데이트되지 않음
                .status(InboundStatus.INSPECTING)
                .build();

        when(inboundRepository.findById(inboundId)).thenReturn(Optional.of(existingInbound));
        when(inboundRepository.save(any(Inbound.class))).thenReturn(updatedInbound);

        // when
        Inbound result = inboundService.updateStatus(inboundId, updateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getInboundId()).isEqualTo(inboundId);
        assertThat(result.getStatus()).isEqualTo(InboundStatus.INSPECTING);
        assertThat(result.getWarehouseId()).isEqualTo(1L); // 업데이트되지 않음
        assertThat(result.getSupplierId()).isEqualTo(1L); // 업데이트되지 않음
        assertThat(result.getProducts()).hasSize(1); // 업데이트되지 않음
        assertThat(result.getProducts().get(0).quantity()).isEqualTo(10); // 업데이트되지 않음

        verify(inboundRepository).findById(inboundId);
        verify(inboundRepository).save(any(Inbound.class));
    }

    @DisplayName("존재하지 않는 입고 상태 수정 시 예외가 발생한다")
    @Test
    void updateStatusWithNotFound() {
        // given
        Long inboundId = 999L;
        UpdateInboundStatusRequest updateRequest = new UpdateInboundStatusRequest(InboundStatus.INSPECTING);

        when(inboundRepository.findById(inboundId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> inboundService.updateStatus(inboundId, updateRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);

        verify(inboundRepository).findById(inboundId);
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
        Long inboundId = 1L;
        List<InboundProduct> inboundProducts = List.of(
                new InboundProduct(1L, 10)
        );

        Inbound inbound = Inbound.builder()
                .inboundId(inboundId)
                .warehouseId(1L)
                .supplierId(1L)
                .expectedDate(LocalDate.of(2024, 1, 15))
                .products(inboundProducts)
                .status(InboundStatus.REGISTERED)
                .build();

        when(inboundRepository.findById(inboundId)).thenReturn(Optional.of(inbound));

        // when
        inboundService.deleteById(inboundId);

        // then
        verify(inboundRepository).findById(inboundId);
        verify(inboundRepository).deleteById(inboundId);
    }

    @DisplayName("존재하지 않는 입고 삭제 시 예외가 발생한다")
    @Test
    void deleteByIdWithNotFound() {
        // given
        Long inboundId = 999L;
        when(inboundRepository.findById(inboundId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> inboundService.deleteById(inboundId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.DATA_NOT_FOUND);

        verify(inboundRepository).findById(inboundId);
    }

    @DisplayName("null ID로 입고 삭제 시 예외가 발생한다")
    @Test
    void deleteByIdWithNullId() {
        // when & then
        assertThatThrownBy(() -> inboundService.deleteById(null))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.INVALID_INPUT);
    }
}
