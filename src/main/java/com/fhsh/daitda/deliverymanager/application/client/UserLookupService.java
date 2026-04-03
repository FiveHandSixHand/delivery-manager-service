package com.fhsh.daitda.deliverymanager.application.client;

import com.fhsh.daitda.deliverymanager.application.command.UserInfoCommand;
import com.fhsh.daitda.deliverymanager.domain.exception.DeliveryManagerErrorCode;
import com.fhsh.daitda.deliverymanager.infrastructure.external.UserFeignClient;
import com.fhsh.daitda.deliverymanager.infrastructure.external.dto.UserResponse;
import com.fhsh.daitda.exception.BusinessException;
import com.fhsh.daitda.response.CommonResponse;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserLookupService {

    private final UserFeignClient userFeignClient;

    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackGetUser")
    public UserInfoCommand getUser(UUID userId) {
        CommonResponse<UserResponse> response = userFeignClient.getUser(userId);
        UserResponse data = response.getData();

        if (data == null) {
            throw new BusinessException(DeliveryManagerErrorCode.USER_NOT_FOUND);
        }

        return new UserInfoCommand(
                data.userId(),
                data.hubId(),
                data.slackUserId()
        );
    }

    public UserInfoCommand fallbackGetUser(UUID userId, Throwable t) {
        log.error("user-service 호출 실패. userId={}, cause={}", userId, t.getMessage(), t);

        if (t instanceof FeignException.NotFound) {
            throw new BusinessException(DeliveryManagerErrorCode.USER_NOT_FOUND);
        }

        throw new BusinessException(DeliveryManagerErrorCode.USER_SERVICE_ERROR);
    }
}
