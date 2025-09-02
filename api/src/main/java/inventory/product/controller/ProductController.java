package inventory.product.controller;

import inventory.exception.CustomException;
import inventory.exception.ExceptionCode;
import inventory.product.controller.request.CreateProductRequest;
import inventory.product.controller.request.UpdateProductRequest;
import inventory.product.controller.response.ProductResponse;
import inventory.response.ApiResponse;
import inventory.response.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static inventory.exception.ExceptionCode.RESOURCE_NOT_FOUND;
import static inventory.supplier.controller.SupplierController.SUPPLIER_STORE;

@RequestMapping("/api/v1/products")
@RestController
public class ProductController {
    
    public static final AtomicLong ID_GENERATOR = new AtomicLong();
    public static final Map<Long, ProductResponse> PRODUCT_STORE = new ConcurrentHashMap<>();

    private static final String DEFAULT_PAGE_NUMBER = "0";
    private static final String DEFAULT_PAGE_SIZE = "50";
    private static final String DEFAULT_THUMBNAIL = "DEFAULT";

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        if (!SUPPLIER_STORE.containsKey(request.supplierId())) {
            throw new CustomException(ExceptionCode.DATA_NOT_FOUND);
        }

        Long id = ID_GENERATOR.getAndIncrement();

        ProductResponse productResponse = ProductResponse.of(
                id,
                request.productName(),
                request.supplierId(),
                SUPPLIER_STORE.get(request.supplierId()).name(),
                request.productCode(),
                request.thumbnailUrl() == null ? DEFAULT_THUMBNAIL : request.thumbnailUrl(),
                request.unit(),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        PRODUCT_STORE.put(id, productResponse);

        return ResponseEntity.ok(ApiResponse.success(productResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> searchProduct(
            @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER) final int currentPageNumber,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pageSize
    ) {
        List<ProductResponse> products = PRODUCT_STORE.values().stream()
                .sorted(Comparator.comparing(ProductResponse::createdAt).reversed())
                .toList();

        long totalElements = products.size();
        int startIndex = currentPageNumber * pageSize;
        int endIndex = Math.min(startIndex + pageSize, products.size());

        List<ProductResponse> pagedProducts = products.subList(startIndex, endIndex);

        PageResponse<ProductResponse> pageResponse = PageResponse.of(
                pagedProducts,
                currentPageNumber,
                pageSize,
                totalElements
        );

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable final Long id) {
        ProductResponse foundProduct = Optional.ofNullable(PRODUCT_STORE.get(id))
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        Optional.ofNullable(SUPPLIER_STORE.get(foundProduct.supplierId()))
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success(foundProduct));
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable final Long id,
            @Valid @RequestBody final UpdateProductRequest request
    ) {
        ProductResponse currentProduct = Optional.ofNullable(PRODUCT_STORE.get(id))
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        ProductResponse updatedProduct = ProductResponse.of(
                id,
                request.productName(),
                currentProduct.supplierId(),
                currentProduct.supplierName(),
                currentProduct.productCode(),
                request.thumbnailUrl() == null ? DEFAULT_THUMBNAIL : request.thumbnailUrl(),
                currentProduct.unit(),
                request.active(),
                currentProduct.createdAt(),
                LocalDateTime.now()
        );

        PRODUCT_STORE.put(id, updatedProduct);

        return ResponseEntity.ok(ApiResponse.success(updatedProduct));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable final Long id) {
        if (!PRODUCT_STORE.containsKey(id)) {
            throw new CustomException(RESOURCE_NOT_FOUND);
        }

        PRODUCT_STORE.remove(id);

        return ResponseEntity.noContent().build();
    }
}
