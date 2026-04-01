package com.fhsh.daitda.deliverymanager.infrastructure.repository;

import com.fhsh.daitda.deliverymanager.domain.entity.DeliveryManager;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import com.fhsh.daitda.deliverymanager.domain.repository.DeliveryManagerQueryRepository;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.fhsh.daitda.deliverymanager.domain.entity.QDeliveryManager.deliveryManager;

@Repository
@RequiredArgsConstructor
public class DeliveryManagerQueryRepositoryImpl implements DeliveryManagerQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<DeliveryManager> findByTypeAndHubIdOrderBySequence(DeliveryManagerType type, UUID hubId) {
        return queryFactory
                .selectFrom(deliveryManager)
                .where(
                        deliveryManager.type.eq(type),
                        eqHubId(hubId),
                        deliveryManager.deletedAt.isNull()
                )
                .orderBy(deliveryManager.sequence.asc())
                .fetch();
    }

    @Override
    public List<DeliveryManager> findByTypeAndHubIdStartingFromSequence(
            DeliveryManagerType type,
            UUID hubId,
            int startSequence
    ) {
        return queryFactory
                .selectFrom(deliveryManager)
                .where(
                        deliveryManager.type.eq(type),
                        eqHubId(hubId),
                        deliveryManager.deletedAt.isNull()
                )
                .orderBy(
                        sequencePriority(startSequence),
                        deliveryManager.sequence.asc()
                )
                .fetch();
    }

    private com.querydsl.core.types.Predicate eqHubId(UUID hubId) {
        return hubId == null
                ? deliveryManager.managerInfo.hubId.isNull()
                : deliveryManager.managerInfo.hubId.eq(hubId);
    }

    private OrderSpecifier<Integer> sequencePriority(int startSequence) {
        return new CaseBuilder()
                .when(deliveryManager.sequence.goe(startSequence)).then(0)
                .otherwise(1)
                .asc();
    }
}
