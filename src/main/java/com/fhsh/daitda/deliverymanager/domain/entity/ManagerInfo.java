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

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "hub_id")
    private UUID hubId;

    @Column(name = "slack_id", length = 100, nullable = false)
    private String slackId;

    protected ManagerInfo(UUID userId, UUID hubId, String slackId) {
        validate(userId, slackId);

        this.userId = userId;
        this.hubId = hubId;
        this.slackId = slackId;
    }

    // 생성
    public static ManagerInfo of(UUID userId, UUID hubId, String slackId) {
        return new ManagerInfo(userId, hubId, slackId);
    }

    // 허브 소속인지 확인
    public boolean hasHub() {
        return this.hubId != null;
    }

    // 특정 허브의 소속인지 확인
    public boolean belongsTo(UUID hubId) {
        return this.hubId != null && this.hubId.equals(hubId);
    }

    // 슬랙 아이디 변경
    public ManagerInfo withSlackId(String slackId) {
        return new ManagerInfo(this.userId, this.hubId, slackId);
    }

    // 허브 아이디 변경
    public ManagerInfo withHubId(UUID hubId) {
        return new ManagerInfo(this.userId, hubId, this.slackId);
    }

    private static void validate(UUID userId, String slackId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }
        if (slackId == null || slackId.isBlank()) {
            throw new IllegalArgumentException("slackId는 필수입니다.");
        }
    }
}
