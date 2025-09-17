package inventory.supplier.controller;

import inventory.common.dto.response.ApiResponse;
import inventory.common.dto.response.PageResponse;
import inventory.supplier.service.request.CreateSupplierRequest;
import inventory.supplier.service.request.UpdateSupplierRequest;
import inventory.supplier.service.response.SupplierResponse;
import inventory.supplier.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @RequestParam(defaultValue = "0") int currentPageNumber,
            @RequestParam(defaultValue = "30") int pageSize) {

        List<SupplierResponse> suppliers = supplierService.findAll();

        int startIndex = currentPageNumber * pageSize;
        int endIndex = Math.min(startIndex + pageSize, suppliers.size());
        List<SupplierResponse> pagedSuppliers = suppliers.subList(startIndex, endIndex);

        PageResponse<SupplierResponse> pageResponse =
                PageResponse.of(pagedSuppliers, currentPageNumber, pageSize, suppliers.size());

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