package inventory.warehouse.controller;

import inventory.common.dto.response.ApiResponse;
import inventory.common.dto.response.PageResponse;
import inventory.warehouse.service.WarehouseService;
import inventory.warehouse.service.request.CreateWarehouseRequest;
import inventory.warehouse.service.request.UpdateWarehouseRequest;
import inventory.warehouse.service.response.WarehouseResponse;
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
@RequestMapping("/api/v1/warehouses")
@RestController
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    public ResponseEntity<ApiResponse<WarehouseResponse>> createWarehouse(
            @Valid @RequestBody CreateWarehouseRequest request) {
        WarehouseResponse response = warehouseService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, response));
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<WarehouseResponse>> getWarehouse(@PathVariable Long id) {
        WarehouseResponse response = warehouseService.findById(id);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<WarehouseResponse>>> searchWarehouse(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String postcode,
            @RequestParam(required = false) Boolean active) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<WarehouseResponse> pageResult = warehouseService.findAllWithConditions(
                name, postcode, active, pageable
        );

        PageResponse<WarehouseResponse> pageResponse = PageResponse.of(
                pageResult.getContent(), page, size, pageResult.getTotalElements()
        );

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<WarehouseResponse>> updateWarehouse(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWarehouseRequest request) {

        WarehouseResponse response = warehouseService.update(id, request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}