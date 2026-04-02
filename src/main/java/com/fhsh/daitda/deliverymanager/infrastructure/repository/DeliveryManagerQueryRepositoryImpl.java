package com.fhsh.daitda.deliverymanager.infrastructure.repository;

import com.fhsh.daitda.deliverymanager.domain.entity.DeliveryManager;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import com.fhsh.daitda.deliverymanager.domain.repository.DeliveryManagerQueryRepository;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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

    // 생성일순 목록 조회
    @Override
    public List<DeliveryManager> findAllByHubIdAndTypeOrderByCreatedAtAsc(UUID hubId, DeliveryManagerType type) {
        return queryFactory
                .selectFrom(deliveryManager)
                .where(
                        isNotDeleted(),
                        eqHubId(hubId),
                        eqType(type)
                )
                .orderBy(createdAtAsc(), updatedAtAsc())
                .fetch();
    }

    // 수정일순 목록 조회
    @Override
    public List<DeliveryManager> findAllByHubIdAndTypeOrderByUpdatedAtAsc(UUID hubId, DeliveryManagerType type) {
        return queryFactory
                .selectFrom(deliveryManager)
                .where(
                        isNotDeleted(),
                        eqHubId(hubId),
                        eqType(type)
                )
                .orderBy(updatedAtAsc(), createdAtAsc())
                .fetch();
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

    // 생성일 오름차순
    private OrderSpecifier<?> createdAtAsc() {
        return deliveryManager.createdAt.asc();
    }

    // 수정일 오름차순
    private OrderSpecifier<?> updatedAtAsc() {
        return deliveryManager.updatedAt.asc();
    }
}
