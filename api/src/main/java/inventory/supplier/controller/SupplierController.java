package inventory.supplier.controller;

import inventory.supplier.controller.request.CreateSupplierRequest;
import inventory.supplier.controller.request.UpdateSupplierRequest;
import inventory.supplier.controller.response.SupplierResponse;
import inventory.exception.CustomException;
import inventory.response.ApiResponse;
import inventory.response.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

@RequestMapping("/api/v1/suppliers")
@RestController
public class SupplierController {

    static final AtomicLong ID_GENERATOR = new AtomicLong(1);
    static final Map<Long, SupplierResponse> SUPPLIER_STORE = new ConcurrentHashMap<>();

    private static final String DEFAULT_PAGE_NUMBER = "0";
    private static final String DEFAULT_PAGE_SIZE = "30";

    @PostMapping
    public ResponseEntity<ApiResponse<SupplierResponse>> createSupplier(@Valid @RequestBody final CreateSupplierRequest request) {
        Long id = ID_GENERATOR.getAndIncrement();

        SupplierResponse createdSupplier = SupplierResponse.of(
                id,
                request.name(),
                request.businessRegistrationNumber(),
                request.postcode(),
                request.baseAddress(),
                request.detailAddress(),
                request.ceoName(),
                request.managerName(),
                request.managerContact(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        SUPPLIER_STORE.put(id, createdSupplier);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, createdSupplier));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SupplierResponse>>> searchSupplier(
            @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER) final int currentPageNumber,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pageSize
    ) {
        List<SupplierResponse> suppliers = SUPPLIER_STORE.values().stream()
                .sorted(Comparator.comparing(SupplierResponse::createdAt).reversed())
                .toList();

        long totalElements = suppliers.size();
        int startIndex = currentPageNumber * pageSize;
        int endIndex = Math.min(startIndex + pageSize, suppliers.size());

        List<SupplierResponse> pagedSuppliers = suppliers.subList(startIndex, endIndex);

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
                foundSupplier.name(),
                foundSupplier.businessRegistrationNumber(),
                request.postcode() != null ? request.postcode() : foundSupplier.postcode(),
                request.baseAddress() != null ? request.baseAddress() : foundSupplier.baseAddress(),
                request.detailAddress() != null ? request.detailAddress() : foundSupplier.detailAddress(),
                request.ceoName() != null ? request.ceoName() : foundSupplier.ceoName(),
                request.managerName() != null ? request.managerName() : foundSupplier.managerName(),
                request.managerContact() != null ? request.managerContact() : foundSupplier.managerContact(),
                foundSupplier.createdAt(),
                LocalDateTime.now()
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
