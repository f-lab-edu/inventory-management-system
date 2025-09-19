package inventory.warehouse.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inventory.warehouse.domain.QWarehouse;
import inventory.warehouse.service.query.WarehouseSearchCondition;
import inventory.warehouse.service.response.WarehouseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WarehouseQueryRepositoryImpl implements WarehouseQueryRepository {

    private static final QWarehouse warehouse = QWarehouse.warehouse;
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<WarehouseResponse> findWarehouseSummaries(WarehouseSearchCondition condition, Pageable pageable) {
        BooleanExpression where = warehouse.isNotNull();
        if (condition != null) {
            if (condition.nameContains() != null && !condition.nameContains().isBlank())
                where = where.and(warehouse.name.containsIgnoreCase(condition.nameContains()));
            if (condition.postcodeContains() != null && !condition.postcodeContains().isBlank())
                where = where.and(warehouse.postcode.containsIgnoreCase(condition.postcodeContains()));
            if (condition.active() != null) where = where.and(warehouse.active.eq(condition.active()));
        }

        Long total = queryFactory.select(warehouse.count())
                .from(warehouse)
                .where(where)
                .fetchOne();

        var content = queryFactory
                .select(Projections.constructor(WarehouseResponse.class,
                        warehouse.warehouseId,
                        warehouse.name,
                        warehouse.postcode,
                        warehouse.baseAddress,
                        warehouse.detailAddress,
                        warehouse.managerName,
                        warehouse.managerContact,
                        warehouse.active,
                        warehouse.createdAt,
                        warehouse.modifiedAt
                ))
                .from(warehouse)
                .where(where)
                .orderBy(warehouse.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }
}


