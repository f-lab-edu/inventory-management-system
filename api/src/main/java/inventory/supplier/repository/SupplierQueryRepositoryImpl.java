package inventory.supplier.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inventory.supplier.domain.QSupplier;
import inventory.supplier.service.query.SupplierSearchCondition;
import inventory.supplier.service.response.SupplierResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SupplierQueryRepositoryImpl implements SupplierQueryRepository {

    private static final QSupplier supplier = QSupplier.supplier;
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SupplierResponse> findSupplierSummaries(SupplierSearchCondition condition, Pageable pageable) {
        BooleanExpression where = supplier.isNotNull();
        if (condition != null) {
            if (condition.nameContains() != null && !condition.nameContains().isBlank())
                where = where.and(supplier.name.containsIgnoreCase(condition.nameContains()));
            if (condition.brnContains() != null && !condition.brnContains().isBlank())
                where = where.and(supplier.businessRegistrationNumber.containsIgnoreCase(condition.brnContains()));
            if (condition.active() != null) where = where.and(supplier.active.eq(condition.active()));
        }

        Long total = queryFactory.select(supplier.count())
                .from(supplier)
                .where(where)
                .fetchOne();

        var content = queryFactory
                .select(Projections.constructor(SupplierResponse.class,
                        supplier.supplierId,
                        supplier.name,
                        supplier.businessRegistrationNumber,
                        supplier.postcode,
                        supplier.baseAddress,
                        supplier.detailAddress,
                        supplier.ceoName,
                        supplier.managerName,
                        supplier.managerContact,
                        supplier.active,
                        supplier.createdAt,
                        supplier.modifiedAt
                ))
                .from(supplier)
                .where(where)
                .orderBy(supplier.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }
}


