package com.fhsh.daitda.deliverymanager.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

// Spring SecurityContext에서 현재 인증된 사용자 정보를 조회하기 위한 유틸 클래스
public final class SecurityContextUtils {

    // 유틸 클래스이므로 외부에서 인스턴스를 생성하지 못하도록 제한
    private SecurityContextUtils() {
    }

    // 현재 SecurityContext에 저장된 인증 주체를 CustomPrincipal 형태로 반환
    public static CustomPrincipal getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomPrincipal principal)) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        return principal;
    }
}
