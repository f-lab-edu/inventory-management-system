package inventory.warehouse.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inventory.product.domain.QProduct;
import inventory.warehouse.domain.QWarehouse;
import inventory.warehouse.domain.QWarehouseStock;
import inventory.warehouse.service.query.WarehouseStockSearchCondition;
import inventory.warehouse.service.response.WarehouseStockResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WarehouseStockQueryRepositoryImpl implements WarehouseStockQueryRepository {

    private static final QWarehouseStock warehouseStock = QWarehouseStock.warehouseStock;
    private static final QWarehouse warehouse = QWarehouse.warehouse;
    private static final QProduct product = QProduct.product;

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<WarehouseStockResponse> findWarehouseStockSummaries(WarehouseStockSearchCondition condition, Pageable pageable) {
        BooleanExpression where = warehouseStock.isNotNull();
        if (condition != null) {
            if (condition.warehouseId() != null) where = where.and(warehouseStock.warehouseId.eq(condition.warehouseId()));
            if (condition.productId() != null) where = where.and(warehouseStock.productId.eq(condition.productId()));
            if (condition.productNameContains() != null && !condition.productNameContains().isBlank())
                where = where.and(product.productName.containsIgnoreCase(condition.productNameContains()));
            if (condition.productCodeContains() != null && !condition.productCodeContains().isBlank())
                where = where.and(product.productCode.containsIgnoreCase(condition.productCodeContains()));
            if (condition.belowSafetyOnly() != null && condition.belowSafetyOnly())
                where = where.and(warehouseStock.quantity.lt(warehouseStock.safetyStock));
        }

        Long total = queryFactory.select(warehouseStock.count())
                .from(warehouseStock)
                .leftJoin(warehouse).on(warehouse.warehouseId.eq(warehouseStock.warehouseId))
                .leftJoin(product).on(product.productId.eq(warehouseStock.productId))
                .where(where)
                .fetchOne();

        var content = queryFactory
                .select(Projections.constructor(WarehouseStockResponse.class,
                        warehouseStock.warehouseStockId,
                        warehouseStock.warehouseId,
                        warehouse.name,
                        warehouseStock.productId,
                        product.productName,
                        product.productCode,
                        warehouseStock.quantity,
                        warehouseStock.safetyStock,
                        warehouseStock.quantity.lt(warehouseStock.safetyStock),
                        warehouseStock.modifiedAt
                ))
                .from(warehouseStock)
                .leftJoin(warehouse).on(warehouse.warehouseId.eq(warehouseStock.warehouseId))
                .leftJoin(product).on(product.productId.eq(warehouseStock.productId))
                .where(where)
                .orderBy(warehouseStock.modifiedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }
}


