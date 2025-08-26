package inventory.supplier.controller;

import inventory.common.api.ApiResponse;
import inventory.common.api.PageResponse;
import inventory.common.exception.CustomException;
import inventory.supplier.dto.request.UpdateSupplierRequest;
import inventory.supplier.dto.request.CreateSupplierRequest;
import inventory.supplier.dto.response.SupplierResponse;
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

@RequestMapping("/api/v1/suppliers")
@RestController
public class SupplierController {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);
    private static final Map<Long, SupplierResponse> SUPPLIER_STORE = new ConcurrentHashMap<>();

    private static final String DEFAULT_PAGE_NUMBER = "0";
    private static final String DEFAULT_PAGE_SIZE = "30";

    @PostMapping
    public ResponseEntity<ApiResponse<SupplierResponse>> createSupplier(@Valid @RequestBody final CreateSupplierRequest request) {
        Long id = ID_GENERATOR.getAndIncrement();

        SupplierResponse Supplier = SupplierResponse.of(
                id,
                request.name(),
                request.businessRegistrationNumber(),
                request.ceoName(),
                request.postcode(),
                request.baseAddress(),
                request.detailAddress(),
                request.contact()
        );

        SUPPLIER_STORE.put(id, Supplier);

        return ResponseEntity.created(URI.create("/api/v1/suppliers/" + id))
                .body(ApiResponse.success(HttpStatus.CREATED, Supplier));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SupplierResponse>>> searchSupplier(
            @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER) final int currentPageNumber,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pageSize
    ) {
        List<SupplierResponse> allSuppliers = SUPPLIER_STORE.values().stream()
                .sorted(Comparator.comparing(SupplierResponse::id).reversed())
                .toList();

        long totalElements = allSuppliers.size();
        int startIndex = currentPageNumber * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allSuppliers.size());

        List<SupplierResponse> pagedSuppliers = allSuppliers.subList(startIndex, endIndex);

        PageResponse<SupplierResponse> pageResponse = PageResponse.of(
                pagedSuppliers,
                currentPageNumber,
                pageSize,
                totalElements
        );

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<SupplierResponse>> getSupplier(@PathVariable final Long id) {
        SupplierResponse foundSupplier = Optional.ofNullable(SUPPLIER_STORE.get(id))
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success(foundSupplier));
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<SupplierResponse>> updateSupplier(
            @PathVariable final Long id,
            @Valid @RequestBody final UpdateSupplierRequest request
    ) {
        SupplierResponse foundSupplier = Optional.ofNullable(SUPPLIER_STORE.get(id))
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        SupplierResponse updatedSupplier = SupplierResponse.of(
                id,
                request.name() != null ? request.name() : foundSupplier.name(),
                request.businessRegistrationNumber() != null ? request.businessRegistrationNumber() : foundSupplier.businessRegistrationNumber(),
                request.ceoName() != null ? request.ceoName() : foundSupplier.ceoName(),
                request.postcode() != null ? request.postcode() : foundSupplier.postcode(),
                request.baseAddress() != null ? request.baseAddress() : foundSupplier.baseAddress(),
                request.detailAddress() != null ? request.detailAddress() : foundSupplier.detailAddress(),
                request.contact() != null ? request.contact() : foundSupplier.contact()
        );

        SUPPLIER_STORE.put(id, updatedSupplier);

        return ResponseEntity.ok(ApiResponse.success(updatedSupplier));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable final Long id) {
        if (!SUPPLIER_STORE.containsKey(id)) {
            throw new CustomException(RESOURCE_NOT_FOUND);
        }

        SUPPLIER_STORE.remove(id);

        return ResponseEntity.noContent().build();
    }
}
