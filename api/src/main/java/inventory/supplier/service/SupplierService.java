package inventory.supplier.service;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.supplier.domain.Supplier;
import inventory.supplier.repository.SupplierRepository;
import inventory.supplier.service.query.SupplierSearchCondition;
import inventory.supplier.service.request.CreateSupplierRequest;
import inventory.supplier.service.request.UpdateSupplierRequest;
import inventory.supplier.service.response.SupplierResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierResponse save(CreateSupplierRequest request) {
        Supplier supplier = Supplier.builder()
                .name(request.name())
                .businessRegistrationNumber(request.businessRegistrationNumber())
                .postcode(request.postcode())
                .baseAddress(request.baseAddress())
                .detailAddress(request.detailAddress())
                .ceoName(request.ceoName())
                .managerName(request.managerName())
                .managerContact(request.managerContact())
                .build();

        return SupplierResponse.from(supplierRepository.save(supplier));
    }

    @Transactional(readOnly = true)
    public SupplierResponse findById(Long id) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        return SupplierResponse.from(supplierRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND)));
    }

    @Transactional(readOnly = true)
    public Page<SupplierResponse> findAllWithConditions(
            String nameContains,
            String brnContains,
            Boolean active,
            Pageable pageable
    ) {
        SupplierSearchCondition condition = new SupplierSearchCondition(
                nameContains, brnContains, active
        );
        return supplierRepository.findSupplierSummaries(condition, pageable);
    }

    public SupplierResponse update(Long id, UpdateSupplierRequest request) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));

        return SupplierResponse.from(existingSupplier.update(request.postcode(), request.baseAddress(),
                request.detailAddress(), request.ceoName(), request.managerName(), request.managerContact()));
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));
        supplier.softDelete();
    }
}
