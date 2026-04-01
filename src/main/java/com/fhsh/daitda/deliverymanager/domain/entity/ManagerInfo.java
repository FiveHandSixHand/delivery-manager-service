package com.fhsh.daitda.deliverymanager.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ManagerInfo {

    @Column(nullable = false)
    private UUID userId;

    private UUID hubId;

    @Column(length = 100, nullable = false)
    private String slackId;

    @Builder
    protected ManagerInfo(UUID userId, UUID hubId, String slackId) {
        this.userId = userId;
        this.hubId = hubId;
        this.slackId = slackId;
    }
}
