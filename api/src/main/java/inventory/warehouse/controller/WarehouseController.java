package inventory.warehouse.controller;

import inventory.common.dto.response.ApiResponse;
import inventory.common.dto.response.PageResponse;
import inventory.warehouse.service.request.CreateWarehouseRequest;
import inventory.warehouse.service.request.UpdateWarehouseRequest;
import inventory.warehouse.service.response.WarehouseResponse;
import inventory.warehouse.domain.Warehouse;
import inventory.warehouse.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @RequestParam(defaultValue = "0") int currentPageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {

        List<Warehouse> warehouses = warehouseService.findAll();

        int startIndex = currentPageNumber * pageSize;
        int endIndex = Math.min(startIndex + pageSize, warehouses.size());
        List<Warehouse> pagedWarehouses = warehouses.subList(startIndex, endIndex);

        List<WarehouseResponse> responses = pagedWarehouses.stream()
                .map(WarehouseResponse::from)
                .toList();

        PageResponse<WarehouseResponse> pageResponse = PageResponse.of(
                responses, currentPageNumber, pageSize, warehouses.size());

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