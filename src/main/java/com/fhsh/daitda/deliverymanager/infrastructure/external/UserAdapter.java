package com.fhsh.daitda.deliverymanager.infrastructure.external;

import com.fhsh.daitda.deliverymanager.application.client.UserClient;
import com.fhsh.daitda.deliverymanager.application.command.UserInfoCommand;
import com.fhsh.daitda.deliverymanager.infrastructure.external.dto.UserResponse;
import com.fhsh.daitda.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserAdapter implements UserClient {

    private final UserFeignClient userFeignClient;

    @Override
    public UserInfoCommand getUser(UUID userId) {
        CommonResponse<UserResponse> feignResponse = userFeignClient.getUser(userId);
        UserResponse response = feignResponse.getData();

        if (response == null) {
            return null;
        }

        return new UserInfoCommand(response.userId(), response.hubId(), response.slackUserId(), response.role());
    }
}
