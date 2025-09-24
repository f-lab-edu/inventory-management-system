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
import inventory.outbound.service.response.OutboundProductResponse;
import inventory.outbound.service.response.OutboundResponse;
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

import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
@Service
public class OutboundService {

    private final OutboundRepository outboundRepository;
    private final OutboundProductRepository outboundProductRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseStockRepository warehouseStockRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OutboundResponse createOutbound(CreateOutboundRequest request) {
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

        return OutboundResponse.from(savedOutbound, warehouse, outboundProductResponses);
    }

    private void validateStockAvailability(Long warehouseId, List<OutboundProductRequest> products) {
        // 1. 상품 ID 목록 추출
        List<Long> productIds = products.stream()
                .map(OutboundProductRequest::productId)
                .toList();

        // 2. 상품 정보 일괄 조회
        List<Product> productList = productRepository.findByIds(productIds);
        Map<Long, Product> productMap = productList.stream()
                .collect(toMap(Product::getProductId, p -> p));

        // 3. 재고 정보 일괄 조회
        List<WarehouseStock> stockList = warehouseStockRepository.findByWarehouseIdAndProductIdIn(warehouseId, productIds);
        Map<Long, WarehouseStock> stockMap = stockList.stream()
                .collect(toMap(WarehouseStock::getProductId, s -> s));

        // 4. 검증
        for (OutboundProductRequest productRequest : products) {
            Product product = productMap.get(productRequest.productId());
            if (product == null) {
                throw new CustomException(ExceptionCode.DATA_NOT_FOUND,
                        "상품을 찾을 수 없습니다. 상품 ID: " + productRequest.productId());
            }

            WarehouseStock warehouseStock = stockMap.get(productRequest.productId());
            if (warehouseStock == null) {
                throw new CustomException(ExceptionCode.STOCK_NOT_FOUND,
                        "창고에 해당 상품의 재고가 없습니다. 상품: " + product.getProductName());
            }

            if (!warehouseStock.hasEnoughStock(productRequest.quantity())) {
                throw new CustomException(ExceptionCode.INSUFFICIENT_STOCK,
                        String.format("재고가 부족합니다. 상품: %s, 현재 재고: %d, 요청 수량: %d",
                                product.getProductName(),
                                warehouseStock.getQuantity(),
                                productRequest.quantity()));
            }
        }
    }

    @Transactional
    public void startPicking(Long outboundId) {
        Outbound outbound = outboundRepository.findById(outboundId)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND, "출고를 찾을 수 없습니다."));

        outbound.updateStatus(OutboundStatus.PICKING);
    }

    @Transactional
    public void completeOutbound(Long outboundId) {
        Outbound outbound = outboundRepository.findById(outboundId)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND, "출고를 찾을 수 없습니다."));

        outbound.updateStatus(OutboundStatus.SHIPPED);

        // 예약 재고를 실제 재고에서 차감
        List<OutboundProduct> outboundProducts = outboundProductRepository.findByOutboundId(outboundId);
        Map<Long, WarehouseStock> stockMap = getWarehouseStockMap(outbound.getWarehouseId(), outboundProducts);

        // 재고 차감 처리
        for (OutboundProduct outboundProduct : outboundProducts) {
            WarehouseStock stock = stockMap.get(outboundProduct.getProductId());
            if (stock == null) {
                throw new CustomException(ExceptionCode.STOCK_NOT_FOUND);
            }
            stock.confirmShipment(outboundProduct.getRequestedQuantity());
        }
    }

    @Transactional
    public void cancelOutbound(Long outboundId) {
        Outbound outbound = outboundRepository.findById(outboundId)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND, "출고를 찾을 수 없습니다."));

        if (!outbound.canBeCanceled()) {
            throw new CustomException(ExceptionCode.INVALID_STATE, "취소할 수 없는 상태입니다.");
        }

        outbound.updateStatus(OutboundStatus.CANCELED);

        // 예약 재고 해제
        List<OutboundProduct> outboundProducts = outboundProductRepository.findByOutboundId(outboundId);
        Map<Long, WarehouseStock> stockMap = getWarehouseStockMap(outbound.getWarehouseId(), outboundProducts);

        // 예약 재고 해제 처리
        for (OutboundProduct outboundProduct : outboundProducts) {
            WarehouseStock stock = stockMap.get(outboundProduct.getProductId());
            if (stock == null) {
                throw new CustomException(ExceptionCode.STOCK_NOT_FOUND);
            }
            stock.releaseReservation(outboundProduct.getRequestedQuantity());
        }
    }

    @Transactional(readOnly = true)
    public OutboundResponse findById(Long id) {
        if (id == null) {
            throw new CustomException(ExceptionCode.INVALID_INPUT);
        }

        Outbound outbound = outboundRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));

        Warehouse warehouse = warehouseRepository.findById(outbound.getWarehouseId())
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND));

        List<OutboundProduct> outboundProducts = outboundProductRepository.findByOutboundId(outbound.getOutboundId());

        List<Long> productIds = outboundProducts.stream()
                .map(OutboundProduct::getProductId)
                .toList();

        Map<Long, Product> productMap = productRepository.findByIds(productIds).stream()
                .collect(toMap(Product::getProductId, p -> p));

        Map<Long, WarehouseStock> stockMap = warehouseStockRepository.findByWarehouseIdAndProductIdIn(
                        outbound.getWarehouseId(), productIds).stream()
                .collect(toMap(WarehouseStock::getProductId, s -> s));

        List<OutboundProductResponse> outboundProductResponses = outboundProducts.stream()
                .map(outboundProduct -> {
                    Product product = productMap.get(outboundProduct.getProductId());
                    WarehouseStock warehouseStock = stockMap.get(outboundProduct.getProductId());
                    return OutboundProductResponse.from(outboundProduct, product, warehouseStock);
                })
                .toList();

        return OutboundResponse.from(outbound, warehouse, outboundProductResponses);
    }

    private Map<Long, WarehouseStock> getWarehouseStockMap(Long warehouseId, List<OutboundProduct> outboundProducts) {
        List<Long> productIds = outboundProducts.stream()
                .map(OutboundProduct::getProductId)
                .toList();

        List<WarehouseStock> stockList = warehouseStockRepository.findByWarehouseIdAndProductIdIn(warehouseId, productIds);
        return stockList.stream()
                .collect(toMap(WarehouseStock::getProductId, s -> s));
    }
}
