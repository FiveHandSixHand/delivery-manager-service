package com.fhsh.daitda.deliverymanager.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_EMAIL_HEADER = "X-User-Email";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String userId = request.getHeader(USER_ID_HEADER);
            String email = request.getHeader(USER_EMAIL_HEADER);
            String role = request.getHeader(USER_ROLE_HEADER);

            if (StringUtils.hasText(userId)
                    && StringUtils.hasText(email)
                    && StringUtils.hasText(role)) {

                CustomPrincipal principal = new CustomPrincipal(
                        UUID.fromString(userId),
                        email,
                        role
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(SecurityUtils.createAuthentication(principal));
            }

            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
