package com.fhsh.daitda.deliverymanager.infrastructure.repository;

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
                        notDelivering(),
                        sequenceFrom(startSequence)
                )
                .orderBy(deliveryManager.sequence.asc())
                .fetchFirst();

        if (candidate != null) {
            return Optional.of(candidate);
        }

        DeliveryManager wrappedCandidate = queryFactory
                .selectFrom(deliveryManager)
                .where(
                        typeEq(type),
                        hubIdEq(type, hubId),
                        notDeleted(),
                        notDelivering()
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

    private BooleanExpression notDelivering() {
        return deliveryManager.isDelivery.isFalse();
    }

    private BooleanExpression sequenceFrom(int startSequence) {
        return deliveryManager.sequence.goe(startSequence);
    }
}
