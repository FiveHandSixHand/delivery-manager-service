package com.fhsh.daitda.deliverymanager.infrastructure.repository;

import com.fhsh.daitda.deliverymanager.domain.entity.AssignmentCursor;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import com.fhsh.daitda.deliverymanager.domain.repository.AssignmentCursorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AssignmentCursorRepositoryImpl implements AssignmentCursorRepository {

    private final AssignmentCursorJpaRepository assignmentCursorJpaRepository;

    @Override
    public Optional<AssignmentCursor> findByTypeAndHubId(DeliveryManagerType type, UUID hubId) {
        return assignmentCursorJpaRepository.findByTypeAndHubId(type, hubId);
    }

    @Override
    public AssignmentCursor save(AssignmentCursor assignmentCursor) {
        return assignmentCursorJpaRepository.save(assignmentCursor);
    }
}
