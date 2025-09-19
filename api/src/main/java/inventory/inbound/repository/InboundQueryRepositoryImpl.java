package inventory.inbound.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inventory.inbound.domain.Inbound;
import inventory.inbound.domain.QInbound;
import inventory.inbound.service.response.InboundSummaryResponse;
import inventory.supplier.domain.QSupplier;
import inventory.warehouse.domain.QWarehouse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class InboundQueryRepositoryImpl implements InboundQueryRepository {

    private static final QInbound inbound = QInbound.inbound;
    private static final QWarehouse warehouse = QWarehouse.warehouse;
    private static final QSupplier supplier = QSupplier.supplier;

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Inbound> findInboundsWithConditions(
            InboundSearchCondition condition,
            Pageable pageable
    ) {
        BooleanExpression whereClause = createWhereClause(condition);

        Long totalCount = queryFactory
                .select(inbound.count())
                .from(inbound)
                .where(whereClause)
                .fetchOne();

        // 페이징된 데이터 조회
        var results = queryFactory
                .selectFrom(inbound)
                .where(whereClause)
                .orderBy(inbound.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageable, totalCount != null ? totalCount : 0L);
    }

    @Override
    public Page<InboundSummaryResponse> findInboundSummaries(
            InboundSearchCondition condition,
            Pageable pageable
    ) {
        BooleanExpression whereClause = createWhereClause(condition);

        Long totalCount = queryFactory
                .select(inbound.count())
                .from(inbound)
                .where(whereClause)
                .fetchOne();

        List<InboundSummaryResponse> content = queryFactory
                .select(Projections.constructor(InboundSummaryResponse.class,
                        inbound.inboundId,
                        inbound.warehouseId,
                        warehouse.name,
                        inbound.supplierId,
                        supplier.name,
                        inbound.expectedDate,
                        inbound.status,
                        inbound.createdAt,
                        inbound.modifiedAt
                ))
                .from(inbound)
                .leftJoin(warehouse).on(warehouse.warehouseId.eq(inbound.warehouseId))
                .leftJoin(supplier).on(supplier.supplierId.eq(inbound.supplierId))
                .where(whereClause)
                .orderBy(inbound.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, totalCount == null ? 0L : totalCount);
    }

    private BooleanExpression createWhereClause(InboundSearchCondition condition) {
        BooleanExpression whereClause = inbound.isNotNull();

        if (condition.warehouseId() != null) {
            whereClause = whereClause.and(inbound.warehouseId.eq(condition.warehouseId()));
        }
        if (condition.supplierId() != null) {
            whereClause = whereClause.and(inbound.supplierId.eq(condition.supplierId()));
        }
        if (condition.status() != null) {
            whereClause = whereClause.and(inbound.status.eq(condition.status()));
        }
        if (condition.startDate() != null) {
            whereClause = whereClause.and(inbound.expectedDate.goe(condition.startDate()));
        }
        if (condition.endDate() != null) {
            whereClause = whereClause.and(inbound.expectedDate.loe(condition.endDate()));
        }

        return whereClause;
    }
}
