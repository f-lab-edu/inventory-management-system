package inventory.outbound.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inventory.outbound.domain.QOutbound;
import inventory.outbound.service.query.OutboundSearchCondition;
import inventory.outbound.service.response.OutboundSummaryResponse;
import inventory.warehouse.domain.QWarehouse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OutboundQueryRepositoryImpl implements OutboundQueryRepository {

    private static final QOutbound outbound = QOutbound.outbound;
    private static final QWarehouse warehouse = QWarehouse.warehouse;

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<OutboundSummaryResponse> findOutboundSummaries(
            OutboundSearchCondition condition,
            Pageable pageable
    ) {
        BooleanExpression whereClause = createWhereClause(condition);

        Long totalCount = queryFactory
                .select(outbound.count())
                .from(outbound)
                .leftJoin(warehouse).on(warehouse.warehouseId.eq(outbound.warehouseId))
                .where(whereClause)
                .fetchOne();

        List<OutboundSummaryResponse> content = queryFactory
                .select(Projections.constructor(OutboundSummaryResponse.class,
                        outbound.outboundId,
                        outbound.orderNumber,
                        outbound.warehouseId,
                        warehouse.name,
                        outbound.recipientName,
                        outbound.requestedDate,
                        outbound.expectedDate,
                        outbound.outboundStatus,
                        outbound.createdAt
                ))
                .from(outbound)
                .leftJoin(warehouse).on(warehouse.warehouseId.eq(outbound.warehouseId))
                .where(whereClause)
                .orderBy(outbound.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, totalCount == null ? 0L : totalCount);
    }

    private BooleanExpression createWhereClause(OutboundSearchCondition condition) {
        BooleanExpression whereClause = outbound.isNotNull();

        if (condition != null) {
            if (condition.orderNumber() != null && !condition.orderNumber().isBlank()) {
                whereClause = whereClause.and(outbound.orderNumber.containsIgnoreCase(condition.orderNumber()));
            }
            if (condition.warehouseId() != null) {
                whereClause = whereClause.and(outbound.warehouseId.eq(condition.warehouseId()));
            }
            if (condition.status() != null) {
                whereClause = whereClause.and(outbound.outboundStatus.eq(condition.status()));
            }
            if (condition.startDate() != null) {
                whereClause = whereClause.and(outbound.requestedDate.goe(condition.startDate()));
            }
            if (condition.endDate() != null) {
                whereClause = whereClause.and(outbound.requestedDate.loe(condition.endDate()));
            }
        }

        return whereClause;
    }
}
