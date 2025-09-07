package inventory.product.service;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.product.controller.request.CreateProductRequest;
import inventory.product.controller.request.UpdateProductRequest;
import inventory.product.domain.Product;
import inventory.product.repository.ProductRepository;
import inventory.supplier.repository.SupplierRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public Product save(CreateProductRequest request) {
        supplierRepository.findById(request.supplierId())
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));

        Product product = Product.builder()
                .supplierId(request.supplierId())
                .productName(request.productName())
                .productCode(request.productCode())
                .unit(request.unit())
                .thumbnailUrl(request.thumbnailUrl())
                .build();

        return productRepository.save(product);
    }

    public Product findById(Long id) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        return productRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product update(Long id, UpdateProductRequest request) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        Product existingProduct = findById(id);

        Product updatedProduct = Product.builder()
                .productId(id)
                .supplierId(existingProduct.getSupplierId())
                .productCode(existingProduct.getProductCode())
                .unit(existingProduct.getUnit())
                .productName(request.productName() != null ? request.productName() : existingProduct.getProductName())
                .thumbnailUrl(request.thumbnailUrl())
                .active(request.active())
                .build();

        return productRepository.save(updatedProduct);
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

