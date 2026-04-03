package com.fhsh.daitda.deliverymanager.infrastructure.security;

import java.util.UUID;

// Gateway에서 전달받은 헤더 정보를 해당 객체에 담아 SecurityContext에 저장
public record CustomPrincipal(
        UUID userId,
        String email,
        String role
) {
    public boolean hasRole(String role) {
        return this.role != null && this.role.equals(role);
    }
}
