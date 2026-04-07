package com.fhsh.daitda.deliverymanager.infrastructure.persistence.repository;

import com.fhsh.daitda.deliverymanager.domain.entity.DeliveryManager;
import com.fhsh.daitda.deliverymanager.domain.entity.QDeliveryManager;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import com.fhsh.daitda.deliverymanager.domain.repository.DeliveryManagerAssignmentRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryManagerAssignmentRepositoryImpl implements DeliveryManagerAssignmentRepository {

    private final JPAQueryFactory queryFactory;

    private static final QDeliveryManager deliveryManager = QDeliveryManager.deliveryManager;

    @Override
    public Optional<DeliveryManager> findNextAssignable(
            DeliveryManagerType type, UUID hubId, int startSequence
    ) {
        DeliveryManager candidate = queryFactory
                .selectFrom(deliveryManager)
                .where(
                        typeEq(type),
                        hubIdEq(type, hubId),
                        notDeleted(),
                        sequenceBetween(startSequence, 10)
                )
                .orderBy(deliveryManager.sequence.asc())
                .fetchFirst();

        // 시작 번호 ~ 10번 중 찾은 경우
        if (candidate != null) {
            return Optional.of(candidate);
        }

        // 1번 ~ 시작 번호 전 중 찾도록 다시 순회
        DeliveryManager wrappedCandidate = queryFactory
                .selectFrom(deliveryManager)
                .where(
                        typeEq(type),
                        hubIdEq(type, hubId),
                        notDeleted(),
                        sequenceBetween(1, startSequence - 1)
                )
                .orderBy(deliveryManager.sequence.asc())
                .fetchFirst();

        return Optional.ofNullable(wrappedCandidate);
    }

    private BooleanExpression typeEq(DeliveryManagerType type) {
        return deliveryManager.type.eq(type);
    }

    private BooleanExpression hubIdEq(DeliveryManagerType type, UUID hubId) {
        if (type == DeliveryManagerType.HUB) {
            return deliveryManager.managerInfo.hubId.isNull();
        }
        return deliveryManager.managerInfo.hubId.eq(hubId);
    }

    private BooleanExpression notDeleted() {
        return deliveryManager.deletedAt.isNull();
    }

    private BooleanExpression sequenceBetween(int start, int end) {
        return deliveryManager.sequence.between(start, end);
    }
}
