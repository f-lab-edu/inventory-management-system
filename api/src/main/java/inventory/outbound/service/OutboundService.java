package inventory.outbound.service;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.outbound.domain.Outbound;
import inventory.outbound.domain.OutboundProduct;
import inventory.outbound.domain.enums.OutboundStatus;
import inventory.outbound.repository.OutboundProductRepository;
import inventory.outbound.repository.OutboundRepository;
import inventory.outbound.service.request.CreateOutboundRequest;
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
import java.util.List;

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
                .orderNumber("order-number")
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

        // 출고 상품 등록 및 재고 차감
        List<OutboundProductResponse> outboundProductResponses = new ArrayList<>();

        for (inventory.outbound.service.request.OutboundProductRequest productRequest : request.products()) {
            // 상품 존재 여부 확인
            Product product = productRepository.findById(productRequest.productId())
                    .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND,
                            "상품을 찾을 수 없습니다. 상품 ID: " + productRequest.productId()));

            // 출고 상품 등록
            OutboundProduct outboundProduct = OutboundProduct.builder()
                    .outboundId(savedOutbound.getOutboundId())
                    .productId(productRequest.productId())
                    .requestedQuantity(productRequest.quantity())
                    .build();

            OutboundProduct savedOutboundProduct = outboundProductRepository.save(outboundProduct);

            // 재고 조회 (차감 전)
            WarehouseStock warehouseStock = warehouseStockRepository
                    .findByWarehouseIdAndProductId(warehouse.getWarehouseId(), productRequest.productId())
                    .orElseThrow(() -> new CustomException(ExceptionCode.STOCK_NOT_FOUND,
                            "창고에 해당 상품의 재고가 없습니다. 상품: " + product.getProductName()));

            // 응답용 OutboundProductResponse 생성 (재고 차감 전 상태로)
            OutboundProductResponse productResponse = OutboundProductResponse.from(
                    savedOutboundProduct,
                    product,
                    warehouseStock
            );
            outboundProductResponses.add(productResponse);
        }

        return CreateOutboundResponse.from(savedOutbound, warehouse, outboundProductResponses);
    }

    private void validateStockAvailability(Long warehouseId, java.util.List<inventory.outbound.service.request.OutboundProductRequest> products) {
        for (inventory.outbound.service.request.OutboundProductRequest productRequest : products) {
            // 상품 존재 여부 확인
            Product product = productRepository.findById(productRequest.productId())
                    .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND,
                            "상품을 찾을 수 없습니다. 상품 ID: " + productRequest.productId()));

            // 재고 존재 여부 및 수량 확인
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
