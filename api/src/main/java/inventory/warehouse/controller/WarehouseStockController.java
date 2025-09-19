package inventory.warehouse.controller;

import inventory.common.dto.response.ApiResponse;
import inventory.common.dto.response.PageResponse;
import inventory.warehouse.service.WarehouseStockService;
import inventory.warehouse.service.response.WarehouseStockResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse-stocks")
@RestController
public class WarehouseStockController {

    private final WarehouseStockService warehouseStockService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<WarehouseStockResponse>>> searchWarehouseStocks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "modifiedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String productCode,
            @RequestParam(required = false) Boolean belowSafetyOnly
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<WarehouseStockResponse> result = warehouseStockService.findAllWithConditions(
                warehouseId, productId, productName, productCode, belowSafetyOnly, pageable
        );

        PageResponse<WarehouseStockResponse> pageResponse = PageResponse.of(
                result.getContent(), page, size, result.getTotalElements()
        );

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }
}
