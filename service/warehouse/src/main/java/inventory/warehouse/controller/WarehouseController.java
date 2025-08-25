package inventory.warehouse.controller;

import inventory.common.api.ApiResponse;
import inventory.common.exception.CustomException;
import inventory.warehouse.dto.request.CreateWarehouseRequest;
import inventory.warehouse.dto.response.WarehouseResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static inventory.common.exception.ExceptionCode.RESOURCE_NOT_FOUND;

@RequestMapping("/api/v1/warehouses")
@RestController
public class WarehouseController {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);
    private static final Map<Long, WarehouseResponse> WAREHOUSE_STORE = new ConcurrentHashMap<>();

    @PostMapping
    public ResponseEntity<ApiResponse<WarehouseResponse>> createWarehouse(@Valid @RequestBody final CreateWarehouseRequest request) {

        Long id = ID_GENERATOR.getAndIncrement();

        WarehouseResponse warehouse = WarehouseResponse.of(
                id,
                request.name(),
                request.postcode(),
                request.baseAddress(),
                request.detailAddress(),
                request.managerName(),
                request.managerContact()
        );

        WAREHOUSE_STORE.put(id, warehouse);

        return ResponseEntity.created(URI.create("/api/v1/warehouses/" + id))
                .body(ApiResponse.success(HttpStatus.CREATED, warehouse));
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<WarehouseResponse>> getWarehouse(@PathVariable final Long id) {
        WarehouseResponse foundWarehouse = Optional.ofNullable(WAREHOUSE_STORE.get(id))
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success(foundWarehouse));
    }
}
