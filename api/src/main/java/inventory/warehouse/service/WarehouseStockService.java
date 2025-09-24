package inventory.warehouse.service;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.product.repository.ProductRepository;
import inventory.warehouse.domain.WarehouseStock;
import inventory.warehouse.repository.WarehouseRepository;
import inventory.warehouse.repository.WarehouseStockQueryRepository;
import inventory.warehouse.repository.WarehouseStockRepository;
import inventory.warehouse.service.query.WarehouseStockSearchCondition;
import inventory.warehouse.service.response.WarehouseStockResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class WarehouseStockService {

    private final WarehouseStockRepository warehouseStockRepository;
    private final WarehouseStockQueryRepository warehouseStockQueryRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void updateStockOnInbound(Long warehouseId, Long productId, int quantity) {
        if (warehouseId == null || productId == null || quantity <= 0) {
            throw new CustomException(ExceptionCode.INVALID_INPUT, "유효하지 않은 입력값입니다.");
        }

        // 창고와 상품 존재 여부 확인
        warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND, "창고를 찾을 수 없습니다."));

        productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ExceptionCode.DATA_NOT_FOUND, "상품을 찾을 수 없습니다."));

        // 기존 재고 조회
        WarehouseStock existingStock = warehouseStockRepository
                .findByWarehouseIdAndProductId(warehouseId, productId)
                .orElse(null);

        if (existingStock != null) {
            // 기존 상품이 있으면 재고 증가
            existingStock.increaseStock(quantity);
        } else {
            // 기존 상품이 없으면 새로 등록
            WarehouseStock newStock = WarehouseStock.builder()
                    .warehouseId(warehouseId)
                    .productId(productId)
                    .quantity(quantity)
                    .safetyStock(0) // 기본 안전재고 0으로 설정
                    .build();
            warehouseStockRepository.save(newStock);
        }
    }

    @Transactional(readOnly = true)
    public Page<WarehouseStockResponse> findAllWithConditions(
            Long warehouseId,
            Long productId,
            String productNameContains,
            String productCodeContains,
            Boolean belowSafetyOnly,
            Pageable pageable
    ) {
        WarehouseStockSearchCondition condition = new WarehouseStockSearchCondition(
                warehouseId, productId, productNameContains, productCodeContains, belowSafetyOnly
        );
        return warehouseStockQueryRepository.findWarehouseStockSummaries(condition, pageable);
    }

}
