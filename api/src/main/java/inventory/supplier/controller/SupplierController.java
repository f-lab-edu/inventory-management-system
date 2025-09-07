package inventory.supplier.controller;

import inventory.common.dto.response.ApiResponse;
import inventory.common.dto.response.PageResponse;
import inventory.supplier.controller.request.CreateSupplierRequest;
import inventory.supplier.controller.request.UpdateSupplierRequest;
import inventory.supplier.controller.response.SupplierResponse;
import inventory.supplier.domain.Supplier;
import inventory.supplier.service.SupplierService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/suppliers")
@RestController
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<ApiResponse<SupplierResponse>> createSupplier(
            @Valid @RequestBody CreateSupplierRequest request) {
        Supplier savedSupplier = supplierService.save(request);
        SupplierResponse response = SupplierResponse.from(savedSupplier);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, response));
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<SupplierResponse>> getSupplier(@PathVariable Long id) {
        Supplier supplier = supplierService.findById(id);
        SupplierResponse response = SupplierResponse.from(supplier);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SupplierResponse>>> searchSupplier(
            @RequestParam(defaultValue = "0") int currentPageNumber, @RequestParam(defaultValue = "30") int pageSize) {

        List<Supplier> suppliers = supplierService.findAll();

        int startIndex = currentPageNumber * pageSize;
        int endIndex = Math.min(startIndex + pageSize, suppliers.size());
        List<Supplier> pagedSuppliers = suppliers.subList(startIndex, endIndex);

        List<SupplierResponse> responses = pagedSuppliers.stream().map(SupplierResponse::from).toList();

        PageResponse<SupplierResponse> pageResponse = PageResponse.of(responses, currentPageNumber, pageSize,
                suppliers.size());

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<SupplierResponse>> updateSupplier(@PathVariable Long id,
                                                                        @Valid @RequestBody UpdateSupplierRequest request) {

        Supplier updatedSupplier = supplierService.update(id, request);
        SupplierResponse response = SupplierResponse.from(updatedSupplier);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}