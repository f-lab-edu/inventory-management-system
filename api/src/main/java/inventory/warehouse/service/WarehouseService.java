package inventory.warehouse.service;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.warehouse.domain.Warehouse;
import inventory.warehouse.repository.WarehouseRepository;
import inventory.warehouse.service.query.WarehouseSearchCondition;
import inventory.warehouse.service.request.CreateWarehouseRequest;
import inventory.warehouse.service.request.UpdateWarehouseRequest;
import inventory.warehouse.service.response.WarehouseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
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

    public Page<WarehouseResponse> findAllWithConditions(
            String nameContains,
            String postcodeContains,
            Boolean active,
            Pageable pageable
    ) {
        WarehouseSearchCondition condition = new WarehouseSearchCondition(
                nameContains, postcodeContains, active
        );
        return warehouseRepository.findWarehouseSummaries(condition, pageable);
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
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));
        warehouse.softDelete();
    }
}
