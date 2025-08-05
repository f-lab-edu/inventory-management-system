package io.inventory.warehouse.command.service;

import io.inventory.warehouse.command.dto.request.WarehouseCreateRequest;
import io.inventory.warehouse.command.repository.WarehouseRepository;
import io.inventory.warehouse.domain.Warehouse;
import io.inventory.warehouse.query.dto.WarehouseDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class WarehouseCommandService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseDetailResponse createWarehouse(final WarehouseCreateRequest request) {
        Warehouse warehouse = warehouseRepository.save(Warehouse.builder()
                .name(request.name())
                .postcode(request.postcode())
                .baseAddress(request.baseAddress())
                .detailAddress(request.detailAddress())
                .contact(request.contact())
                .build());

        return WarehouseDetailResponse.from(warehouse);
    }
}
