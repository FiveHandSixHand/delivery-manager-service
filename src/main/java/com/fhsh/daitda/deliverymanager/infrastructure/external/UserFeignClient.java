package com.fhsh.daitda.deliverymanager.infrastructure.external;

import com.fhsh.daitda.deliverymanager.infrastructure.external.dto.UserResponse;
import com.fhsh.daitda.deliverymanager.infrastructure.external.fallback.UserClientFallbackFactory;
import com.fhsh.daitda.response.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "user-service",
        path = "/internal/v1/users",
        fallbackFactory = UserClientFallbackFactory.class
)
public interface UserFeignClient {
    @GetMapping("/{userId}")
    CommonResponse<UserResponse> getUser(@PathVariable("userId") UUID userId);
}
