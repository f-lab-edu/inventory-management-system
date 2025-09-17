package inventory.inbound.service;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.inbound.controller.request.CreateInboundRequest;
import inventory.inbound.controller.request.UpdateInboundStatusRequest;
import inventory.inbound.controller.response.InboundProductResponse;
import inventory.inbound.controller.response.InboundResponse;
import inventory.inbound.domain.Inbound;
import inventory.inbound.domain.InboundProduct;
import inventory.inbound.enums.InboundStatus;
import inventory.inbound.repository.InboundProductRepository;
import inventory.inbound.repository.InboundRepository;
import inventory.product.domain.Product;
import inventory.product.repository.ProductRepository;
import inventory.supplier.domain.Supplier;
import inventory.supplier.repository.SupplierRepository;
import inventory.warehouse.domain.Warehouse;
import inventory.warehouse.repository.WarehouseRepository;
import inventory.warehouse.service.WarehouseStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class InboundService {

    private final InboundRepository inboundRepository;
    private final InboundProductRepository inboundProductRepository;
    private final WarehouseRepository warehouseRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final WarehouseStockService warehouseStockService;

    public InboundResponse save(CreateInboundRequest request) {
        Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));

        Supplier supplier = supplierRepository.findById(request.supplierId())
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));

        request.products().forEach(productRequest ->
                productRepository.findById(productRequest.productId())
                        .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND)));

        Inbound inbound = Inbound.builder()
                .warehouseId(request.warehouseId())
                .supplierId(request.supplierId())
                .expectedDate(request.expectedDate())
                .status(InboundStatus.REGISTERED)
                .build();

        Inbound savedInbound = inboundRepository.save(inbound);

        request.products().forEach(productRequest -> {
            InboundProduct inboundProduct = InboundProduct.builder()
                    .inboundId(savedInbound.getInboundId())
                    .productId(productRequest.productId())
                    .quantity(productRequest.quantity())
                    .build();

            inboundProductRepository.save(inboundProduct);
        });

        List<InboundProduct> inboundProducts = inboundProductRepository.findAll().stream()
                .filter(ip -> ip.getInboundId().equals(savedInbound.getInboundId()))
                .toList();

        List<InboundProductResponse> inboundProductResponses = convertToInboundProductResponses(inboundProducts);

        return InboundResponse.from(savedInbound, warehouse, supplier, inboundProductResponses);
    }

    public InboundResponse findById(Long id) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }
        Inbound inbound = inboundRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));
        Warehouse warehouse = warehouseRepository.findById(inbound.getWarehouseId())
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));
        Supplier supplier = supplierRepository.findById(inbound.getSupplierId())
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));
        List<InboundProduct> inboundProducts = inboundProductRepository.findInboundProductsByInboundId(inbound.getInboundId());
        List<InboundProductResponse> inboundProductResponses = convertToInboundProductResponses(inboundProducts);

        return InboundResponse.from(inbound, warehouse, supplier, inboundProductResponses);
    }

    private List<InboundProductResponse> convertToInboundProductResponses(List<InboundProduct> inboundProducts) {
        return inboundProducts.stream()
                .map(inboundProduct -> {
                    Product product = productRepository.findById(inboundProduct.getProductId())
                            .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));
                    return InboundProductResponse.from(inboundProduct, product);
                })
                .toList();
    }

    public List<Inbound> findAll() {
        return inboundRepository.findAll();
    }

    public InboundResponse updateStatus(Long id, UpdateInboundStatusRequest request) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        Inbound inbound = inboundRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));

        // 상태 변경 규칙 검증
        validateStatusTransition(inbound.getStatus(), request.status());

        // 상태 업데이트
        inbound.updateStatus(request.status());

        // 변경사항 저장
        Inbound savedInbound = inboundRepository.save(inbound);

        // 입고 완료 시 창고 재고 업데이트
        if (request.status() == InboundStatus.COMPLETED) {
            updateWarehouseStockOnInboundCompletion(savedInbound);
        }

        // 새로 InboundResponse 생성
        Warehouse warehouse = warehouseRepository.findById(savedInbound.getWarehouseId())
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));

        Supplier supplier = supplierRepository.findById(savedInbound.getSupplierId())
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));

        List<InboundProduct> inboundProducts = inboundProductRepository.findAll().stream()
                .filter(ip -> ip.getInboundId().equals(savedInbound.getInboundId()))
                .toList();

        List<InboundProductResponse> inboundProductResponses = convertToInboundProductResponses(inboundProducts);

        return InboundResponse.from(savedInbound, warehouse, supplier, inboundProductResponses);
    }

    private void validateStatusTransition(InboundStatus currentStatus, InboundStatus newStatus) {
        switch (currentStatus) {
            case REGISTERED:
                if (newStatus != InboundStatus.INSPECTING) {
                    throw new CustomException(ExceptionCode.INVALID_INPUT,
                            "입고 등록 상태에서는 검수 중으로만 변경 가능합니다.");
                }
                break;
            case INSPECTING:
                if (newStatus != InboundStatus.COMPLETED && newStatus != InboundStatus.REJECTED) {
                    throw new CustomException(ExceptionCode.INVALID_INPUT,
                            "검수 중 상태에서는 입고 완료 또는 입고 거절로만 변경 가능합니다.");
                }
                break;
            case COMPLETED:
            case REJECTED:
                throw new CustomException(ExceptionCode.INVALID_INPUT,
                        "입고 완료 또는 입고 거절 상태에서는 더 이상 상태 변경이 불가능합니다.");
            default:
                throw new CustomException(ExceptionCode.INVALID_INPUT,
                        "알 수 없는 상태입니다.");
        }
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        if (inboundRepository.findById(id).isEmpty()) {
            throw new CustomException(ExceptionCode.DATA_NOT_FOUND);
        }

        inboundRepository.deleteById(id);
    }

    /**
     * 입고 완료 시 창고 재고를 업데이트합니다.
     */
    private void updateWarehouseStockOnInboundCompletion(Inbound inbound) {
        List<InboundProduct> inboundProducts = inboundProductRepository.findAll().stream()
                .filter(ip -> ip.getInboundId().equals(inbound.getInboundId()))
                .toList();

        for (InboundProduct inboundProduct : inboundProducts) {
            warehouseStockService.updateStockOnInbound(
                    inbound.getWarehouseId(),
                    inboundProduct.getProductId(),
                    inboundProduct.getQuantity()
            );
        }
    }
}
