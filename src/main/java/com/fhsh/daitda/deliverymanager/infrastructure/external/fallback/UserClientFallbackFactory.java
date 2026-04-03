package com.fhsh.daitda.deliverymanager.infrastructure.external.fallback;

import com.fhsh.daitda.deliverymanager.application.client.UserClient;
import com.fhsh.daitda.deliverymanager.domain.exception.DeliveryManagerErrorCode;
import com.fhsh.daitda.deliverymanager.infrastructure.external.UserFeignClient;
import com.fhsh.daitda.deliverymanager.infrastructure.external.dto.UserResponse;
import com.fhsh.daitda.exception.BusinessException;
import com.fhsh.daitda.response.CommonResponse;
import feign.FeignException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserClientFallbackFactory implements FallbackFactory<UserFeignClient> {

    @Override
    public UserFeignClient create(Throwable cause) {
        return new UserFeignClient() {
            @Override
            public CommonResponse<UserResponse> getUser(UUID userId) {
                if (cause instanceof FeignException.NotFound) {
                    throw new BusinessException(DeliveryManagerErrorCode.USER_NOT_FOUND);
                }

                throw new BusinessException(DeliveryManagerErrorCode.USER_SERVICE_ERROR);
            }
        };
    }
}
