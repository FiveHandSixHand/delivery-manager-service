package com.fhsh.daitda.deliverymanager.infrastructure.external;

import com.fhsh.daitda.deliverymanager.infrastructure.external.dto.ApiResponse;
import com.fhsh.daitda.deliverymanager.infrastructure.external.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service")
public interface UserFeignClient {
    @GetMapping("/internal/v1/users/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable UUID userId);
}
