package inventory.supplier.controller;

import inventory.common.dto.response.ApiResponse;
import inventory.common.dto.response.PageResponse;
import inventory.supplier.service.SupplierService;
import inventory.supplier.service.request.CreateSupplierRequest;
import inventory.supplier.service.request.UpdateSupplierRequest;
import inventory.supplier.service.response.SupplierResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/suppliers")
@RestController
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<ApiResponse<SupplierResponse>> createSupplier(
            @Valid @RequestBody CreateSupplierRequest request) {
        SupplierResponse savedSupplier = supplierService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, savedSupplier));
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<SupplierResponse>> getSupplier(@PathVariable Long id) {
        SupplierResponse response = supplierService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SupplierResponse>>> searchSupplier(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brn,
            @RequestParam(required = false) Boolean active) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<SupplierResponse> pageResult = supplierService.findAllWithConditions(
                name, brn, active, pageable
        );

        PageResponse<SupplierResponse> pageResponse =
                PageResponse.of(pageResult.getContent(), page, size, pageResult.getTotalElements());

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<SupplierResponse>> updateSupplier(
            @PathVariable Long id, @Valid @RequestBody UpdateSupplierRequest request
    ) {
        SupplierResponse response = supplierService.update(id, request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}