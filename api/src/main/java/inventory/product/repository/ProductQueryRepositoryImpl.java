package inventory.product.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inventory.product.domain.QProduct;
import inventory.product.service.query.ProductSearchCondition;
import inventory.product.service.response.ProductResponse;
import inventory.supplier.domain.QSupplier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepositoryImpl implements ProductQueryRepository {

    private static final QProduct product = QProduct.product;
    private static final QSupplier supplier = QSupplier.supplier;

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProductResponse> findProductSummaries(ProductSearchCondition condition, Pageable pageable) {
        BooleanExpression where = product.isNotNull();
        if (condition != null) {
            if (condition.supplierId() != null) where = where.and(product.supplierId.eq(condition.supplierId()));
            if (condition.productNameContains() != null && !condition.productNameContains().isBlank())
                where = where.and(product.productName.containsIgnoreCase(condition.productNameContains()));
            if (condition.productCodeContains() != null && !condition.productCodeContains().isBlank())
                where = where.and(product.productCode.containsIgnoreCase(condition.productCodeContains()));
            if (condition.active() != null) where = where.and(product.active.eq(condition.active()));
        }

        Long total = queryFactory.select(product.count())
                .from(product)
                .where(where)
                .fetchOne();

        var content = queryFactory
                .select(Projections.constructor(ProductResponse.class,
                        product.productId,
                        product.productName,
                        product.supplierId,
                        supplier.name,
                        product.productCode,
                        product.thumbnailUrl,
                        product.unit,
                        product.active,
                        product.createdAt,
                        product.modifiedAt
                ))
                .from(product)
                .leftJoin(supplier).on(supplier.supplierId.eq(product.supplierId))
                .where(where)
                .orderBy(product.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }
}


