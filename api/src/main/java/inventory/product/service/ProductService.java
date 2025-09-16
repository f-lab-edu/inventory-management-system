package inventory.product.service;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.product.controller.request.CreateProductRequest;
import inventory.product.controller.request.UpdateProductRequest;
import inventory.product.controller.response.ProductResponse;
import inventory.product.domain.Product;
import inventory.product.repository.ProductRepository;
import inventory.supplier.domain.Supplier;
import inventory.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
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

        if (!productRepository.findById(id).isPresent()) {
            throw new CustomException(ExceptionCode.DATA_NOT_FOUND);
        }

        productRepository.deleteById(id);
    }
}

