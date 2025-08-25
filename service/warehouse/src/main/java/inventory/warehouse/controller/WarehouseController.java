package inventory.warehouse.controller;

import inventory.warehouse.dto.request.CreateWarehouseRequest;
import inventory.warehouse.dto.response.WarehouseResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RequestMapping("/api/v1/warehouses")
@RestController
public class WarehouseController {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);
    private static final Map<Long, WarehouseResponse> WAREHOUSE_STORE = new ConcurrentHashMap<>();

    @PostMapping
    public ResponseEntity<WarehouseResponse> createWarehouse(@Valid @RequestBody final CreateWarehouseRequest request) {
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
                .body(warehouse);
    }
}
