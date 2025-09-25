package inventory.inbound.service;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.inbound.domain.Inbound;
import inventory.inbound.domain.InboundProduct;
import inventory.inbound.domain.enums.InboundStatus;
import inventory.inbound.repository.InboundProductRepository;
import inventory.inbound.repository.InboundRepository;
import inventory.inbound.service.query.InboundSearchCondition;
import inventory.inbound.service.request.CreateInboundRequest;
import inventory.inbound.service.request.InboundProductRequest;
import inventory.inbound.service.request.UpdateInboundStatusRequest;
import inventory.inbound.service.response.InboundProductResponse;
import inventory.inbound.service.response.InboundResponse;
import inventory.inbound.service.response.InboundSummaryResponse;
import inventory.product.domain.Product;
import inventory.product.repository.ProductRepository;
import inventory.supplier.domain.Supplier;
import inventory.supplier.repository.SupplierRepository;
import inventory.warehouse.domain.Warehouse;
import inventory.warehouse.repository.WarehouseRepository;
import inventory.warehouse.service.WarehouseStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
@Transactional
@Service
public class InboundService {

    private final InboundRepository inboundRepository;
    private final InboundProductRepository inboundProductRepository;
    private final WarehouseRepository warehouseRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final WarehouseStockService warehouseStockService;

    public InboundResponse save(CreateInboundRequest request) {
        Warehouse warehouse = validateAndGetWarehouse(request.warehouseId());
        Supplier supplier = validateAndGetSupplier(request.supplierId());
        validateProducts(request.products());

        Inbound savedInbound = createAndSaveInbound(request);
        saveInboundProducts(savedInbound.getInboundId(), request.products());

        return createInboundResponse(savedInbound, warehouse, supplier);
    }

    @Transactional(readOnly = true)
    public InboundResponse findById(Long id) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        Inbound inbound = inboundRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));

        Warehouse warehouse = validateAndGetWarehouse(inbound.getWarehouseId());
        Supplier supplier = validateAndGetSupplier(inbound.getSupplierId());

        return createInboundResponse(inbound, warehouse, supplier);
    }

    @Transactional(readOnly = true)
    public Page<InboundSummaryResponse> findAllWithConditions(
            Long warehouseId,
            Long supplierId,
            InboundStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        LocalDate defaultStartDate = startDate != null ? startDate : LocalDate.now();
        LocalDate defaultEndDate = endDate != null ? endDate : LocalDate.now();

        InboundSearchCondition condition = new InboundSearchCondition(
                warehouseId, supplierId, status, defaultStartDate, defaultEndDate);

        return inboundRepository.findInboundSummaries(
                condition, pageable
        );
    }

    public InboundResponse updateStatus(Long id, UpdateInboundStatusRequest request) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        Inbound inbound = inboundRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));

        Inbound updatedInbound = inbound.updateStatus(request.status());

        if (request.status() == InboundStatus.COMPLETED) {
            updateWarehouseStockOnInboundCompletion(inbound);
        }
        Warehouse warehouse = validateAndGetWarehouse(inbound.getWarehouseId());
        Supplier supplier = validateAndGetSupplier(inbound.getSupplierId());

        return createInboundResponse(updatedInbound, warehouse, supplier);
    }

    public void cancelInbound(Long id) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        Inbound inbound = inboundRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));
        inbound.updateStatus(InboundStatus.CANCELED);
    }

    public void completeInbound(Long id) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        Inbound inbound = inboundRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));
        Inbound updatedInbound = inbound.updateStatus(InboundStatus.COMPLETED);

        updateWarehouseStockOnInboundCompletion(updatedInbound);
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        inboundRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));

        List<InboundProduct> inboundProducts = inboundProductRepository.findInboundProductsByInboundId(id);
        for (InboundProduct inboundProduct : inboundProducts) {
            inboundProductRepository.deleteById(inboundProduct.getInboundProductId());
        }

        inboundRepository.deleteById(id);
    }

    private Warehouse validateAndGetWarehouse(Long warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));
    }

    private Supplier validateAndGetSupplier(Long supplierId) {
        return supplierRepository.findById(supplierId)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));
    }

    private void validateProducts(List<InboundProductRequest> productRequests) {
        List<Long> productIds = productRequests.stream()
                .map(InboundProductRequest::productId)
                .toList();

        List<Product> products = productRepository.findByIds(productIds);

        if (products.size() != productIds.size()) {
            throw new CustomException(ExceptionCode.DATA_NOT_FOUND);
        }
    }

    private Inbound createAndSaveInbound(CreateInboundRequest request) {
        Inbound inbound = Inbound.builder()
                .warehouseId(request.warehouseId())
                .supplierId(request.supplierId())
                .expectedDate(request.expectedDate())
                .status(InboundStatus.REGISTERED)
                .build();

        return inboundRepository.save(inbound);
    }

    private void saveInboundProducts(Long inboundId, List<InboundProductRequest> productRequests) {
        List<InboundProduct> inboundProducts = productRequests.stream()
                .map(productRequest -> InboundProduct.builder()
                        .inboundId(inboundId)
                        .productId(productRequest.productId())
                        .quantity(productRequest.quantity())
                        .build())
                .toList();

        inboundProductRepository.saveAll(inboundProducts);
    }

    private List<InboundProductResponse> convertToInboundProductResponses(List<InboundProduct> inboundProducts) {
        if (inboundProducts.isEmpty()) {
            return List.of();
        }
        List<Long> productIds = inboundProducts.stream()
                .map(InboundProduct::getProductId)
                .distinct()
                .toList();
        List<Product> products = productRepository.findByIds(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(toMap(Product::getProductId, p -> p));
        return inboundProducts.stream()
                .map(inboundProduct -> {
                    Product product = productMap.get(inboundProduct.getProductId());
                    if (product == null) {
                        throw new CustomException(ExceptionCode.DATA_NOT_FOUND);
                    }
                    return InboundProductResponse.from(inboundProduct, product);
                })
                .toList();
    }

    private InboundResponse createInboundResponse(Inbound inbound, Warehouse warehouse, Supplier supplier) {
        List<InboundProduct> inboundProducts = inboundProductRepository.findInboundProductsByInboundId(inbound.getInboundId());
        List<InboundProductResponse> inboundProductResponses = convertToInboundProductResponses(inboundProducts);

        return InboundResponse.from(inbound, warehouse, supplier, inboundProductResponses);
    }

    private void updateWarehouseStockOnInboundCompletion(Inbound inbound) {
        List<InboundProduct> inboundProducts = inboundProductRepository.findInboundProductsByInboundId(inbound.getInboundId());

        for (InboundProduct inboundProduct : inboundProducts) {
            warehouseStockService.updateStockOnInbound(
                    inbound.getWarehouseId(),
                    inboundProduct.getProductId(),
                    inboundProduct.getQuantity()
            );
        }
    }
}
