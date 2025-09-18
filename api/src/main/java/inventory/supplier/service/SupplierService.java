package inventory.supplier.service;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.supplier.controller.request.CreateSupplierRequest;
import inventory.supplier.controller.request.UpdateSupplierRequest;
import inventory.supplier.domain.Supplier;
import inventory.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public Supplier save(CreateSupplierRequest request) {
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

        return supplierRepository.save(supplier);
    }

    public Supplier findById(Long id) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        return supplierRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));
    }

    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    public Supplier update(Long id, UpdateSupplierRequest request) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        Supplier existingSupplier = findById(id);

        Supplier updateSupplier = Supplier.builder()
                .name(existingSupplier.getName())
                .businessRegistrationNumber(existingSupplier.getBusinessRegistrationNumber())
                .postcode(request.postcode())
                .baseAddress(request.baseAddress())
                .detailAddress(request.detailAddress())
                .ceoName(request.ceoName())
                .managerName(request.managerName())
                .managerContact(request.managerContact())
                .build();

        return existingSupplier.update(updateSupplier);
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        findById(id);
        supplierRepository.deleteById(id);
    }
}
