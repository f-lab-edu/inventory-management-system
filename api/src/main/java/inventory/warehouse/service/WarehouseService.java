package inventory.warehouse.service;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.warehouse.controller.request.CreateWarehouseRequest;
import inventory.warehouse.controller.request.UpdateWarehouseRequest;
import inventory.warehouse.domain.Warehouse;
import inventory.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Warehouse findById(Long id) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        return warehouseRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));
    }

    public List<Warehouse> findAll() {
        return warehouseRepository.findAll();
    }

    public Warehouse update(Long id, UpdateWarehouseRequest request) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        Warehouse existingWarehouse = findById(id);

        Warehouse updateWarehouse = Warehouse.builder()
                .name(request.name())
                .postcode(request.postcode())
                .baseAddress(request.baseAddress())
                .detailAddress(request.detailAddress())
                .managerName(request.managerName())
                .managerContact(request.managerContact())
                .build();

        return existingWarehouse.update(updateWarehouse);
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        findById(id);
        warehouseRepository.deleteById(id);
    }
}
