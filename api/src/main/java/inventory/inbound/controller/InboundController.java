package inventory.inbound.controller;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.inbound.controller.request.CreateInboundRequest;
import inventory.inbound.controller.request.UpdateInboundStatusRequest;
import inventory.inbound.controller.response.InboundResponse;
import inventory.inbound.controller.response.WarehouseProductResponse;
import inventory.inbound.enums.InboundStatus;
import inventory.common.dto.response.ApiResponse;
import inventory.common.dto.response.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static inventory.common.exception.ExceptionCode.RESOURCE_NOT_FOUND;
import static inventory.product.controller.ProductController.PRODUCT_STORE;
import static inventory.supplier.controller.SupplierController.SUPPLIER_STORE;
import static inventory.warehouse.controller.WarehouseController.WAREHOUSE_STORE;

@RequestMapping("/api/v1/inbounds")
@RestController
public class InboundController {

    // 창고 제품 정보 저장소 (창고ID-제품ID 조합으로 키 생성)
    public static final Map<String, WarehouseProductResponse> WAREHOUSE_PRODUCT_STORE = new ConcurrentHashMap<>();

    // 입고 정보 저장소
    public static final Map<Long, InboundResponse> INBOUND_STORE = new ConcurrentHashMap<>();

    // 입고 ID 생성기
    public static final AtomicLong INBOUND_ID_GENERATOR = new AtomicLong();

    // 창고 제품 ID 생성기
    public static final AtomicLong WAREHOUSE_PRODUCT_ID_GENERATOR = new AtomicLong();

    private static final String DEFAULT_PAGE_NUMBER = "0";
    private static final String DEFAULT_PAGE_SIZE = "50";
    private static final Integer DEFAULT_SAFETY_STOCK = 10;

    @PostMapping
    public ResponseEntity<ApiResponse<List<InboundResponse>>> createInbound(@Valid @RequestBody CreateInboundRequest request) {
        List<InboundResponse> createdInbounds = new ArrayList<>();

        // 1. 창고 존재 여부 확인
        if (!WAREHOUSE_STORE.containsKey(request.warehouseId())) {
            throw new CustomException(ExceptionCode.DATA_NOT_FOUND, "창고를 찾을 수 없습니다.");
        }

        // 2. 공급업체 존재 여부 확인
        if (!SUPPLIER_STORE.containsKey(request.supplierId())) {
            throw new CustomException(ExceptionCode.DATA_NOT_FOUND, "공급업체를 찾을 수 없습니다.");
        }

        // 3. 각 상품별로 입고 처리
        for (CreateInboundRequest.InboundProductRequest productRequest : request.products()) {
            // 상품 존재 여부 확인
            if (!PRODUCT_STORE.containsKey(productRequest.productId())) {
                throw new CustomException(ExceptionCode.DATA_NOT_FOUND, "상품을 찾을 수 없습니다.");
            }

            // 해당 창고에 처음으로 상품을 입고하는 경우 창고 상품 등록
            String warehouseProductKey = request.warehouseId() + "-" + productRequest.productId();
            if (!WAREHOUSE_PRODUCT_STORE.containsKey(warehouseProductKey)) {
                registerWarehouseProduct(request.warehouseId(), productRequest.productId());
            }

            // 입고 등록
            Long inboundId = INBOUND_ID_GENERATOR.getAndIncrement();

            InboundResponse inboundResponse = InboundResponse.of(
                    inboundId,
                    request.warehouseId(),
                    WAREHOUSE_STORE.get(request.warehouseId()).name(),
                    productRequest.productId(),
                    PRODUCT_STORE.get(productRequest.productId()).productName(),
                    request.supplierId(),
                    SUPPLIER_STORE.get(request.supplierId()).name(),
                    request.expectedDate(),
                    productRequest.quantity(),
                    InboundStatus.REGISTERED,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            INBOUND_STORE.put(inboundId, inboundResponse);
            createdInbounds.add(inboundResponse);
        }

        return ResponseEntity.ok(ApiResponse.success(createdInbounds));
    }

    private void registerWarehouseProduct(Long warehouseId, Long productId) {
        Long id = WAREHOUSE_PRODUCT_ID_GENERATOR.getAndIncrement();

        WarehouseProductResponse warehouseProduct = WarehouseProductResponse.of(
                id,
                warehouseId,
                WAREHOUSE_STORE.get(warehouseId).name(),
                productId,
                PRODUCT_STORE.get(productId).productName(),
                0, // 초기 재고는 0
                DEFAULT_SAFETY_STOCK,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        String key = warehouseId + "-" + productId;
        WAREHOUSE_PRODUCT_STORE.put(key, warehouseProduct);
    }

    @PutMapping("{id}/status")
    public ResponseEntity<ApiResponse<InboundResponse>> updateInboundStatus(
            @PathVariable final Long id,
            @Valid @RequestBody final UpdateInboundStatusRequest request
    ) {
        InboundResponse currentInbound = Optional.ofNullable(INBOUND_STORE.get(id))
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND, "입고 정보를 찾을 수 없습니다."));

        if (request.status() == InboundStatus.COMPLETED) {
            increaseStock(currentInbound);
        }

        InboundResponse updatedInbound = InboundResponse.of(
                id,
                currentInbound.warehouseId(),
                currentInbound.warehouseName(),
                currentInbound.productId(),
                currentInbound.productName(),
                currentInbound.supplierId(),
                currentInbound.supplierName(),
                currentInbound.expectedDate(),
                currentInbound.quantity(),
                request.status(),
                currentInbound.createdAt(),
                LocalDateTime.now()
        );

        INBOUND_STORE.put(id, updatedInbound);

        return ResponseEntity.ok(ApiResponse.success(updatedInbound));
    }

    private void increaseStock(InboundResponse inbound) {
        String warehouseProductKey = inbound.warehouseId() + "-" + inbound.productId();
        WarehouseProductResponse warehouseProduct = WAREHOUSE_PRODUCT_STORE.get(warehouseProductKey);

        if (warehouseProduct != null) {
            WarehouseProductResponse updatedWarehouseProduct = WarehouseProductResponse.of(
                    warehouseProduct.id(),
                    warehouseProduct.warehouseId(),
                    warehouseProduct.warehouseName(),
                    warehouseProduct.productId(),
                    warehouseProduct.productName(),
                    warehouseProduct.currentStock() + inbound.quantity(),
                    warehouseProduct.safetyStock(),
                    warehouseProduct.createdAt(),
                    LocalDateTime.now()
            );

            WAREHOUSE_PRODUCT_STORE.put(warehouseProductKey, updatedWarehouseProduct);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<InboundResponse>>> searchInbounds(
            @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER) final int currentPageNumber,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pageSize
    ) {
        List<InboundResponse> inbounds = INBOUND_STORE.values().stream()
                .sorted(Comparator.comparing(InboundResponse::createdAt).reversed())
                .toList();

        long totalElements = inbounds.size();
        int startIndex = currentPageNumber * pageSize;
        int endIndex = Math.min(startIndex + pageSize, inbounds.size());

        List<InboundResponse> pagedInbounds = inbounds.subList(startIndex, endIndex);

        PageResponse<InboundResponse> pageResponse = PageResponse.of(
                pagedInbounds,
                currentPageNumber,
                pageSize,
                totalElements
        );

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<InboundResponse>> getInbound(@PathVariable final Long id) {
        InboundResponse inbound = Optional.ofNullable(INBOUND_STORE.get(id))
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND, "입고 정보를 찾을 수 없습니다."));

        return ResponseEntity.ok(ApiResponse.success(inbound));
    }

    @GetMapping("/warehouses/{warehouseId}")
    public ResponseEntity<ApiResponse<List<InboundResponse>>> getInboundsByWarehouse(@PathVariable final Long warehouseId) {
        if (!WAREHOUSE_STORE.containsKey(warehouseId)) {
            throw new CustomException(RESOURCE_NOT_FOUND, "창고를 찾을 수 없습니다.");
        }

        List<InboundResponse> warehouseInbounds = INBOUND_STORE.values().stream()
                .filter(inbound -> inbound.warehouseId().equals(warehouseId))
                .sorted(Comparator.comparing(InboundResponse::createdAt).reversed())
                .toList();

        return ResponseEntity.ok(ApiResponse.success(warehouseInbounds));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<List<InboundResponse>>> getInboundsByProduct(@PathVariable final Long productId) {
        if (!PRODUCT_STORE.containsKey(productId)) {
            throw new CustomException(RESOURCE_NOT_FOUND, "상품을 찾을 수 없습니다.");
        }

        List<InboundResponse> productInbounds = INBOUND_STORE.values().stream()
                .filter(inbound -> inbound.productId().equals(productId))
                .sorted(Comparator.comparing(InboundResponse::createdAt).reversed())
                .toList();

        return ResponseEntity.ok(ApiResponse.success(productInbounds));
    }

    @GetMapping("/suppliers/{supplierId}")
    public ResponseEntity<ApiResponse<List<InboundResponse>>> getInboundsBySupplier(@PathVariable final Long supplierId) {
        if (!SUPPLIER_STORE.containsKey(supplierId)) {
            throw new CustomException(RESOURCE_NOT_FOUND, "공급업체를 찾을 수 없습니다.");
        }

        List<InboundResponse> supplierInbounds = INBOUND_STORE.values().stream()
                .filter(inbound -> inbound.supplierId().equals(supplierId))
                .sorted(Comparator.comparing(InboundResponse::createdAt).reversed())
                .toList();

        return ResponseEntity.ok(ApiResponse.success(supplierInbounds));
    }

    @GetMapping("/warehouses/{warehouseId}/products")
    public ResponseEntity<ApiResponse<List<WarehouseProductResponse>>> getWarehouseProducts(@PathVariable final Long warehouseId) {
        if (!WAREHOUSE_STORE.containsKey(warehouseId)) {
            throw new CustomException(RESOURCE_NOT_FOUND, "창고를 찾을 수 없습니다.");
        }

        List<WarehouseProductResponse> warehouseProducts = WAREHOUSE_PRODUCT_STORE.values().stream()
                .filter(wp -> wp.warehouseId().equals(warehouseId))
                .toList();

        return ResponseEntity.ok(ApiResponse.success(warehouseProducts));
    }
}
