package inventory.inbound.service;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.inbound.controller.request.CreateInboundRequest;
import inventory.inbound.controller.request.UpdateInboundStatusRequest;
import inventory.inbound.controller.response.InboundResponse;
import inventory.inbound.domain.Inbound;
import inventory.inbound.domain.InboundProduct;
import inventory.inbound.enums.InboundStatus;
import inventory.inbound.repository.InboundRepository;
import inventory.product.repository.ProductRepository;
import inventory.supplier.repository.SupplierRepository;
import inventory.warehouse.repository.WarehouseRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class InboundService {

    private final InboundRepository inboundRepository;
    private final WarehouseRepository warehouseRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    public InboundResponse save(CreateInboundRequest request) {
        warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));

        supplierRepository.findById(request.supplierId())
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));

        request.products().forEach(productRequest -> {
            productRepository.findById(productRequest.productId())
                    .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));
        });

        List<InboundProduct> inboundProducts = request.products().stream()
                .map(InboundProduct::from)
                .toList();

        Inbound inbound = Inbound.builder()
                .warehouseId(request.warehouseId())
                .supplierId(request.supplierId())
                .expectedDate(request.expectedDate())
                .products(inboundProducts)
                .status(InboundStatus.REGISTERED)
                .build();

        Inbound savedInbound = inboundRepository.save(inbound);
        return InboundResponse.from(savedInbound);
    }

    public InboundResponse findById(Long id) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        Inbound inbound = inboundRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));
        return InboundResponse.from(inbound);
    }

    public List<InboundResponse> findAll() {
        return inboundRepository.findAll().stream()
                .map(InboundResponse::from)
                .toList();
    }

    public InboundResponse updateStatus(Long id, UpdateInboundStatusRequest request) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        Inbound existingInbound = inboundRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));

        Inbound updateData = Inbound.builder()
                .warehouseId(existingInbound.getWarehouseId())
                .supplierId(existingInbound.getSupplierId())
                .expectedDate(existingInbound.getExpectedDate())
                .products(existingInbound.getProducts())
                .status(request.status())
                .build();

        Inbound updatedInbound = existingInbound.updateStatus(updateData);
        return InboundResponse.from(updatedInbound);
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        if (!inboundRepository.findById(id).isPresent()) {
            throw new CustomException(ExceptionCode.DATA_NOT_FOUND);
        }

        inboundRepository.deleteById(id);
    }
}
