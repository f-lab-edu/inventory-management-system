package inventory.warehouse.controller;

import inventory.common.api.ApiResponse;
import inventory.common.api.PageResponse;
import inventory.common.exception.CustomException;
import inventory.warehouse.dto.request.CreateWarehouseRequest;
import inventory.warehouse.dto.request.UpdateWarehouseRequest;
import inventory.warehouse.dto.response.WarehouseResponse;
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

@RequestMapping("/api/v1/warehouses")
@RestController
public class WarehouseController {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);
    private static final Map<Long, WarehouseResponse> WAREHOUSE_STORE = new ConcurrentHashMap<>();

    private static final String DEFAULT_PAGE_NUMBER = "0";
    private static final String DEFAULT_PAGE_SIZE = "10";

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

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<WarehouseResponse>>> searchWarehouse(
            @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER) final int currentPageNumber,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pageSize
    ) {
        List<WarehouseResponse> allWarehouses = WAREHOUSE_STORE.values().stream()
                .sorted(Comparator.comparing(WarehouseResponse::id).reversed())
                .toList();

        long totalElements = allWarehouses.size();
        int startIndex = currentPageNumber * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allWarehouses.size());

        List<WarehouseResponse> pagedWarehouses = allWarehouses.subList(startIndex, endIndex);

        PageResponse<WarehouseResponse> pageResponse = PageResponse.of(
                pagedWarehouses,
                currentPageNumber,
                pageSize,
                totalElements
        );

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<WarehouseResponse>> getWarehouse(@PathVariable final Long id) {
        WarehouseResponse foundWarehouse = Optional.ofNullable(WAREHOUSE_STORE.get(id))
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success(foundWarehouse));
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<WarehouseResponse>> updateWarehouse(
            @PathVariable final Long id,
            @RequestBody final UpdateWarehouseRequest request
    ) {
        WarehouseResponse foundWarehouse = Optional.ofNullable(WAREHOUSE_STORE.get(id))
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        WarehouseResponse updatedWarehouse = WarehouseResponse.of(
                id,
                request.name() != null ? request.name() : foundWarehouse.name(),
                request.postcode() != null ? request.postcode() : foundWarehouse.postcode(),
                request.baseAddress() != null ? request.baseAddress() : foundWarehouse.baseAddress(),
                request.detailAddress() != null ? request.detailAddress() : foundWarehouse.detailAddress(),
                request.managerName() != null ? request.managerName() : foundWarehouse.managerName(),
                request.managerContact() != null ? request.managerContact() : foundWarehouse.managerContact()
        );

        WAREHOUSE_STORE.put(id, updatedWarehouse);

        return ResponseEntity.ok(ApiResponse.success(updatedWarehouse));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWarehouse(@PathVariable final Long id) {
        if (!WAREHOUSE_STORE.containsKey(id)) {
            throw new CustomException(RESOURCE_NOT_FOUND);
        }

        WAREHOUSE_STORE.remove(id);

        return ResponseEntity.noContent().build();
    }
}
