package inventory.outbound.service;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.outbound.domain.Outbound;
import inventory.outbound.domain.OutboundProduct;
import inventory.outbound.domain.enums.OutboundStatus;
import inventory.outbound.repository.OutboundProductRepository;
import inventory.outbound.repository.OutboundRepository;
import inventory.outbound.service.request.CreateOutboundRequest;
import inventory.outbound.service.request.OutboundProductRequest;
import inventory.outbound.service.response.CreateOutboundResponse;
import inventory.outbound.service.response.OutboundProductResponse;
import inventory.product.domain.Product;
import inventory.product.repository.ProductRepository;
import inventory.warehouse.domain.Warehouse;
import inventory.warehouse.domain.WarehouseStock;
import inventory.warehouse.repository.WarehouseRepository;
import inventory.warehouse.repository.WarehouseStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class OutboundService {

    private final OutboundRepository outboundRepository;
    private final OutboundProductRepository outboundProductRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseStockRepository warehouseStockRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CreateOutboundResponse createOutbound(CreateOutboundRequest request) {
        // 창고를 찾는다.
        Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND, "창고를 찾을 수 없습니다."));

        // 재고 확인 및 검증
        validateStockAvailability(warehouse.getWarehouseId(), request.products());

        // 출고 등록
        Outbound outbound = Outbound.builder()
                .warehouseId(warehouse.getWarehouseId())
                .recipientName(request.recipientName())
                .recipientContact(request.recipientContact())
                .deliveryPostcode(request.deliveryPostcode())
                .deliveryBaseAddress(request.deliveryBaseAddress())
                .deliveryDetailAddress(request.deliveryDetailAddress())
                .requestedDate(request.requestedDate())
                .deliveryMemo(request.deliveryMemo())
                .outboundStatus(OutboundStatus.ORDERED)
                .build();

        Outbound savedOutbound = outboundRepository.save(outbound);

        List<Long> productIds = new ArrayList<>();
        for (OutboundProductRequest pr : request.products()) {
            productIds.add(pr.productId());
        }
        Map<Long, Product> productMap = new HashMap<>();
        for (Product product : productRepository.findByIds(productIds)) {
            productMap.put(product.getProductId(), product);
        }

        Map<Long, WarehouseStock> stockMap = new HashMap<>();
        for (WarehouseStock warehouseStock : warehouseStockRepository.findByWarehouseIdAndProductIdIn(warehouse.getWarehouseId(), productIds)) {
            stockMap.put(warehouseStock.getProductId(), warehouseStock);
        }

        List<OutboundProduct> outboundProducts = new ArrayList<>();
        for (OutboundProductRequest productRequest : request.products()) {
            outboundProducts.add(OutboundProduct.builder()
                    .outboundId(savedOutbound.getOutboundId())
                    .productId(productRequest.productId())
                    .requestedQuantity(productRequest.quantity())
                    .build());
        }

        List<OutboundProduct> savedOutboundProducts = outboundProductRepository.saveAll(outboundProducts);

        for (OutboundProductRequest productRequest : request.products()) {
            WarehouseStock stock = stockMap.get(productRequest.productId());
            stock.reserve(productRequest.quantity());
        }
        warehouseStockRepository.saveAll(new ArrayList<>(stockMap.values()));

        List<OutboundProductResponse> outboundProductResponses = new ArrayList<>();
        for (OutboundProduct savedOutboundProduct : savedOutboundProducts) {
            Product product = productMap.get(savedOutboundProduct.getProductId());
            WarehouseStock warehouseStock = stockMap.get(savedOutboundProduct.getProductId());
            outboundProductResponses.add(OutboundProductResponse.from(savedOutboundProduct, product, warehouseStock));
        }

        return CreateOutboundResponse.from(savedOutbound, warehouse, outboundProductResponses);
    }

    private void validateStockAvailability(Long warehouseId, java.util.List<OutboundProductRequest> products) {
        for (OutboundProductRequest productRequest : products) {
            Product product = productRepository.findById(productRequest.productId())
                    .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND,
                            "상품을 찾을 수 없습니다. 상품 ID: " + productRequest.productId()));

            WarehouseStock warehouseStock = warehouseStockRepository
                    .findByWarehouseIdAndProductId(warehouseId, productRequest.productId())
                    .orElseThrow(() -> new CustomException(ExceptionCode.STOCK_NOT_FOUND,
                            "창고에 해당 상품의 재고가 없습니다. 상품: " + product.getProductName()));

            if (!warehouseStock.hasEnoughStock(productRequest.quantity())) {
                throw new CustomException(ExceptionCode.INSUFFICIENT_STOCK,
                        String.format("재고가 부족합니다. 상품: %s, 현재 재고: %d, 요청 수량: %d",
                                product.getProductName(),
                                warehouseStock.getQuantity(),
                                productRequest.quantity()));
            }
        }
    }
}
