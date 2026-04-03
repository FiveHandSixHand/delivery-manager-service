package com.fhsh.daitda.deliverymanager.infrastructure.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

// CustomPrincipal을 Spring Security의 Authentication 객체로 변환하는 유틸 클래스
public final class SecurityUtils {

    // 유틸 클래스이므로 외부에서 인스턴스를 생성하지 못하도록 제한
    private SecurityUtils() {
    }

    // 사용자 정보를 기반으로 Authentication 객체 생성
    public static Authentication createAuthentication(CustomPrincipal principal) {
        return new UsernamePasswordAuthenticationToken(principal, null,
                List.of(new SimpleGrantedAuthority(principal.role()))
        );
    }
}

