package inventory.product.service;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.product.domain.Product;
import inventory.product.repository.ProductRepository;
import inventory.product.service.request.CreateProductRequest;
import inventory.product.service.request.UpdateProductRequest;
import inventory.product.service.response.ProductResponse;
import inventory.supplier.domain.Supplier;
import inventory.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public ProductResponse save(CreateProductRequest request) {
        Supplier supplier = supplierRepository.findById(request.supplierId())
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));

        Product product = Product.builder()
                .supplierId(request.supplierId())
                .productName(request.productName())
                .productCode(request.productCode())
                .unit(request.unit())
                .thumbnailUrl(request.thumbnailUrl())
                .build();

        return ProductResponse.from(productRepository.save(product), supplier);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));
        Supplier supplier = supplierRepository.findById(product.getSupplierId())
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));

        return ProductResponse.from(product, supplier);
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public ProductResponse update(Long id, UpdateProductRequest request) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));
        Supplier supplier = supplierRepository.findById(existingProduct.getSupplierId())
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));

        return ProductResponse.from(existingProduct.update(request.productName(), request.thumbnailUrl()), supplier);
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));
        product.softDelete();
    }
}

