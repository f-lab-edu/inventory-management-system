package inventory.warehouse.controller;

import inventory.common.dto.response.ApiResponse;
import inventory.common.dto.response.PageResponse;
import inventory.warehouse.controller.request.CreateWarehouseRequest;
import inventory.warehouse.controller.request.UpdateWarehouseRequest;
import inventory.warehouse.controller.response.WarehouseResponse;
import inventory.warehouse.domain.Warehouse;
import inventory.warehouse.service.WarehouseService;
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

@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouses")
@RestController
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    public ResponseEntity<ApiResponse<WarehouseResponse>> createWarehouse(
            @Valid @RequestBody CreateWarehouseRequest request) {
        Warehouse savedWarehouse = warehouseService.save(request);
        WarehouseResponse response = WarehouseResponse.from(savedWarehouse);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, response));
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<WarehouseResponse>> getWarehouse(@PathVariable Long id) {
        Warehouse warehouse = warehouseService.findById(id);
        WarehouseResponse response = WarehouseResponse.from(warehouse);

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

        Warehouse updatedWarehouse = warehouseService.update(id, request);
        WarehouseResponse response = WarehouseResponse.from(updatedWarehouse);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}