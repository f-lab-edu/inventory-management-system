package inventory.product.controller;

import inventory.common.api.ApiResponse;
import inventory.common.api.PageResponse;
import inventory.common.exception.CustomException;
import inventory.product.dto.request.CreateProductRequest;
import inventory.product.dto.request.UpdateProductRequest;
import inventory.product.dto.response.ProductDetailResponse;
import inventory.product.dto.response.ProductResponse;
import inventory.product.dto.response.WarehouseStockResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static inventory.common.exception.ExceptionCode.RESOURCE_NOT_FOUND;

@RequestMapping("/api/v1/products")
@RestController
public class ProductController {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);
    private static final Map<Long, ProductResponse> PRODUCT_STORE = new ConcurrentHashMap<>();

    private static final String DEFAULT_PAGE_NUMBER = "0";
    private static final String DEFAULT_PAGE_SIZE = "30";

    private static final Map<Long, String> DUMMY_SUPPLIER_NAMES = Map.of(
            1L, "삼성전자",
            2L, "LG전자",
            3L, "애플",
            4L, "마이크로소프트",
            5L, "구글"
    );

    private static final Map<Long, String> DUMMY_WAREHOUSE_NAMES = Map.of(
            1L, "서울창고",
            2L, "부산창고",
            3L, "대구창고",
            4L, "인천창고",
            5L, "광주창고"
    );

    private static final Map<Long, String> DUMMY_WAREHOUSE_ADDRESSES = Map.of(
            1L, "서울특별시 강남구 테헤란로 123",
            2L, "부산광역시 해운대구 해운대로 456",
            3L, "대구광역시 수성구 동대구로 789",
            4L, "인천광역시 연수구 송도대로 321",
            5L, "광주광역시 서구 상무대로 654"
    );

    static {
        ProductResponse dummyProduct1 = ProductResponse.of(
                1L, "삼성 갤럭시 S24", "SAMSUNG-123", "스마트폰",
                1L, getSupplierName(1L), 1200000L, "스마트폰"
        );
        ProductResponse dummyProduct2 = ProductResponse.of(
                2L, "LG OLED TV", "LG-123", "TV",
                2L, getSupplierName(2L), 2500000L, "TV"
        );
        ProductResponse dummyProduct3 = ProductResponse.of(
                3L, "애플 맥북 프로", "APPLE-123", "14인치 맥북 프로",
                3L, getSupplierName(3L), 3200000L, "노트북"
        );

        PRODUCT_STORE.put(1L, dummyProduct1);
        PRODUCT_STORE.put(2L, dummyProduct2);
        PRODUCT_STORE.put(3L, dummyProduct3);

        ID_GENERATOR.set(4);
    }

    private static String getSupplierName(Long supplierId) {
        return DUMMY_SUPPLIER_NAMES.getOrDefault(supplierId, "알 수 없는 공급업체");
    }

    private static String getWarehouseName(Long warehouseId) {
        return DUMMY_WAREHOUSE_NAMES.getOrDefault(warehouseId, "알 수 없는 창고");
    }

    private static String getWarehouseAddress(Long warehouseId) {
        return DUMMY_WAREHOUSE_ADDRESSES.getOrDefault(warehouseId, "주소 정보 없음");
    }

    private static List<WarehouseStockResponse> generateDummyWarehouseStocks() {
        return List.of(
                WarehouseStockResponse.of(1L, getWarehouseName(1L), 150L, 50L, getWarehouseAddress(1L)),
                WarehouseStockResponse.of(2L, getWarehouseName(2L), 80L, 30L, getWarehouseAddress(2L)),
                WarehouseStockResponse.of(3L, getWarehouseName(3L), 200L, 100L, getWarehouseAddress(3L))
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody final CreateProductRequest request) {
        Long id = ID_GENERATOR.getAndIncrement();

        ProductResponse product = ProductResponse.of(
                id,
                request.name(),
                request.productCode(),
                request.description(),
                request.supplierId(),
                getSupplierName(request.supplierId()),
                request.price(),
                request.category()
        );

        PRODUCT_STORE.put(id, product);

        return ResponseEntity.created(URI.create("/api/v1/products/" + id))
                .body(ApiResponse.success(HttpStatus.CREATED, product));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> searchProduct(
            @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER) final int currentPageNumber,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pageSize
    ) {
        List<ProductResponse> allProducts = PRODUCT_STORE.values().stream()
                .sorted(Comparator.comparing(ProductResponse::id).reversed())
                .toList();

        long totalElements = allProducts.size();
        int startIndex = currentPageNumber * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allProducts.size());

        List<ProductResponse> pagedProducts = allProducts.subList(startIndex, endIndex);

        PageResponse<ProductResponse> pageResponse = PageResponse.of(
                pagedProducts,
                currentPageNumber,
                pageSize,
                totalElements
        );

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductDetail(@PathVariable final Long id) {
        ProductResponse foundProduct = Optional.ofNullable(PRODUCT_STORE.get(id))
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        List<WarehouseStockResponse> warehouseStocks = generateDummyWarehouseStocks();

        ProductDetailResponse productDetail = ProductDetailResponse.of(
                foundProduct.id(),
                foundProduct.name(),
                foundProduct.productCode(),
                foundProduct.description(),
                foundProduct.supplierId(),
                foundProduct.supplierName(),
                foundProduct.price(),
                foundProduct.category(),
                warehouseStocks
        );

        return ResponseEntity.ok(ApiResponse.success(productDetail));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable final Long id,
            @Valid @RequestBody final UpdateProductRequest request
    ) {
        ProductResponse foundProduct = Optional.ofNullable(PRODUCT_STORE.get(id))
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        Long supplierId = request.supplierId() != null ? request.supplierId() : foundProduct.supplierId();

        ProductResponse updatedProduct = ProductResponse.of(
                id,
                request.name() != null ? request.name() : foundProduct.name(),
                request.productCode() != null ? request.productCode() : foundProduct.productCode(),
                request.description() != null ? request.description() : foundProduct.description(),
                supplierId,
                getSupplierName(supplierId),
                request.price() != null ? request.price() : foundProduct.price(),
                request.category() != null ? request.category() : foundProduct.category()
        );

        PRODUCT_STORE.put(id, updatedProduct);

        return ResponseEntity.ok(ApiResponse.success(updatedProduct));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable final Long id) {
        if (!PRODUCT_STORE.containsKey(id)) {
            throw new CustomException(RESOURCE_NOT_FOUND);
        }

        PRODUCT_STORE.remove(id);

        return ResponseEntity.noContent().build();
    }
}
