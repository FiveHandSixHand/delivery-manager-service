package com.fhsh.daitda.deliverymanager.infrastructure.external;

import com.fhsh.daitda.deliverymanager.application.client.UserClient;
import com.fhsh.daitda.deliverymanager.application.command.UserInfoCommand;
import com.fhsh.daitda.deliverymanager.domain.exception.DeliveryManagerErrorCode;
import com.fhsh.daitda.deliverymanager.infrastructure.external.dto.ApiResponse;
import com.fhsh.daitda.deliverymanager.infrastructure.external.dto.UserResponse;
import com.fhsh.daitda.exception.BusinessException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserAdapter implements UserClient {

    private final UserFeignClient userFeignClient;

    @Override
    public UserInfoCommand getUser(UUID userId) {
        try {
            ApiResponse<UserResponse> apiResponse = userFeignClient.getUser(userId);
            UserResponse response = apiResponse.data();

            if (response == null) {
                return null;
            }

            return new UserInfoCommand(response.userId(), response.hubId(), response.slackUserId());
        } catch (FeignException e) {
            throw new BusinessException(DeliveryManagerErrorCode.USER_SERVICE_ERROR);
        }
    }
}
