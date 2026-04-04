package com.fhsh.daitda.deliverymanager.infrastructure.repository;

import com.fhsh.daitda.deliverymanager.application.query.GetDeliveryManagerListQuery;
import com.fhsh.daitda.deliverymanager.domain.entity.DeliveryManager;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import com.fhsh.daitda.deliverymanager.domain.repository.DeliveryManagerQueryRepository;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.fhsh.daitda.deliverymanager.domain.entity.QDeliveryManager.deliveryManager;

@Repository
@RequiredArgsConstructor
public class DeliveryManagerQueryRepositoryImpl implements DeliveryManagerQueryRepository {

    private final JPAQueryFactory queryFactory;

    // 단건 조회
    @Override
    public Optional<DeliveryManager> findById(UUID deliveryManagerId) {
        DeliveryManager result = queryFactory
                .selectFrom(deliveryManager)
                .where(
                        deliveryManager.deliveryManagerId.eq(deliveryManagerId),
                        isNotDeleted()
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<DeliveryManager> findAll(GetDeliveryManagerListQuery query, Pageable pageable) {
        List<DeliveryManager> contents = queryFactory
                .selectFrom(deliveryManager)
                .where(
                        isNotDeleted(),
                        eqHubId(query.hubId()),
                        eqType(query.type())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(query.sortBy()))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(deliveryManager.count())
                .from(deliveryManager)
                .where(
                        isNotDeleted(),
                        eqHubId(query.hubId()),
                        eqType(query.type())
                );

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }

    @Override
    public Optional<DeliveryManager> findByUserId(UUID userId) {
        DeliveryManager result = queryFactory
                .selectFrom(deliveryManager)
                .where(
                        deliveryManager.managerInfo.userId.eq(userId),
                        isNotDeleted()
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    // 정렬 조건 확인
    private OrderSpecifier<?>[] getOrderSpecifiers(String sortBy) {

        // 수정일순, 생성일순 정렬
        if ("updatedAt".equalsIgnoreCase(sortBy)) {
            return new OrderSpecifier[]{
                    deliveryManager.updatedAt.asc(),
                    deliveryManager.createdAt.asc()
            };
        }

        // 생성일순, 수정일순 정렬
        return new OrderSpecifier[]{
                deliveryManager.createdAt.asc(),
                deliveryManager.updatedAt.asc()
        };
    }

    // 삭제 여부
    private BooleanExpression isNotDeleted() {
        return deliveryManager.deletedAt.isNull();
    }

    // 허브 아이디 확인
    private BooleanExpression eqHubId(UUID hubId) {
        return hubId == null ? null : deliveryManager.managerInfo.hubId.eq(hubId);
    }

    // 타입 확인
    private BooleanExpression eqType(DeliveryManagerType type) {
        return type == null ? null : deliveryManager.type.eq(type);
    }
}
