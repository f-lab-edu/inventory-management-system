package inventory.warehouse.service;

import inventory.warehouse.controller.request.CreateWarehouseRequest;
import inventory.warehouse.domain.Warehouse;
import inventory.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public Warehouse save(CreateWarehouseRequest request) {

        Warehouse warehouse = Warehouse.builder()
                .name(request.name())
                .postcode(request.postcode())
                .baseAddress(request.baseAddress())
                .detailAddress(request.detailAddress())
                .managerName(request.managerName())
                .managerContact(request.managerContact())
                .build();

        return warehouseRepository.save(warehouse);
    }
}
