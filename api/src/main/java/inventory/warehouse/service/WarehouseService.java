package inventory.warehouse.service;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.warehouse.controller.request.CreateWarehouseRequest;
import inventory.warehouse.controller.request.UpdateWarehouseRequest;
import inventory.warehouse.controller.response.WarehouseResponse;
import inventory.warehouse.domain.Warehouse;
import inventory.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseResponse save(CreateWarehouseRequest request) {
        Warehouse warehouse = Warehouse.builder()
                .name(request.name())
                .postcode(request.postcode())
                .baseAddress(request.baseAddress())
                .detailAddress(request.detailAddress())
                .managerName(request.managerName())
                .managerContact(request.managerContact())
                .build();

        return WarehouseResponse.from(warehouseRepository.save(warehouse));
    }

    public WarehouseResponse findById(Long id) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        return WarehouseResponse.from(warehouseRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND)));
    }

    public List<Warehouse> findAll() {
        return warehouseRepository.findAll();
    }

    public WarehouseResponse update(Long id, UpdateWarehouseRequest request) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }
        Warehouse existingWarehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));

        return WarehouseResponse.from(
                existingWarehouse.update(
                        request.name(), request.postcode(),
                        request.baseAddress(), request.detailAddress(),
                        request.managerName(), request.managerContact()));
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        findById(id);
        warehouseRepository.deleteById(id);
    }
}
