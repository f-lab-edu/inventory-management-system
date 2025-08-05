package io.inventory.warehouse.controller;

import io.inventory.common.response.ApiResponse;
import io.inventory.warehouse.command.dto.request.WarehouseCreateRequest;
import io.inventory.warehouse.command.service.WarehouseCommandService;
import io.inventory.warehouse.query.dto.WarehouseDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouses")
@RestController
public class WarehouseController {

    private final WarehouseCommandService warehouseCommandService;

    @PostMapping
    public ResponseEntity<ApiResponse<WarehouseDetailResponse>> createWarehouse(@RequestBody final WarehouseCreateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(warehouseCommandService.createWarehouse(request)));
    }
}
