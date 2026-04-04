package com.fhsh.daitda.deliverymanager.presentation.controller;

import com.fhsh.daitda.deliverymanager.application.command.CreateDeliveryManagerCommand;
import com.fhsh.daitda.deliverymanager.application.result.CreateDeliveryManagerResult;
import com.fhsh.daitda.deliverymanager.application.result.DeleteDeliveryManagerResult;
import com.fhsh.daitda.deliverymanager.application.service.command.DeliveryManagerCommandService;
import com.fhsh.daitda.deliverymanager.presentation.dto.request.CreateDeliveryManagerRequest;
import com.fhsh.daitda.deliverymanager.presentation.dto.response.CreateDeliveryManagerResponse;
import com.fhsh.daitda.deliverymanager.presentation.dto.response.DeleteDeliveryManagerResponse;
import com.fhsh.daitda.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery-managers")
@RestController
public class DeliveryManagerController {

    private final DeliveryManagerCommandService service;

    //@PreAuthorize("hasAnyRole('ADMIN', 'HUB')")
    @PostMapping
    public ResponseEntity<CommonResponse<CreateDeliveryManagerResponse>> createDeliveryManager(
            @RequestHeader(value = "X-User-Role") String role,
            @RequestBody CreateDeliveryManagerRequest request) {

        if (!hasAdminOrHubRole(role)) {
            return forbiddenResponse();
        }

        // Role이 HUB인 경우, 소속 hubId 확인 필요

        CreateDeliveryManagerCommand command = new CreateDeliveryManagerCommand(request.targetUserId(), request.type());

        CreateDeliveryManagerResult result = service.createDeliveryManager(command);

        CreateDeliveryManagerResponse response = new CreateDeliveryManagerResponse(result.deliveryManagerId());

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @DeleteMapping("/{deliveryManagerId}")
    public ResponseEntity<CommonResponse<DeleteDeliveryManagerResponse>> deleteDeliveryManager(
            @RequestHeader(value = "X-User-Id") String userId,
            @RequestHeader(value = "X-User-Role") String role,
            @PathVariable UUID deliveryManagerId) {

        if (!hasAdminOrHubRole(role)) {
            return forbiddenResponse();
        }

        DeleteDeliveryManagerResult result = service.deleteDeliveryManager(userId, deliveryManagerId);

        DeleteDeliveryManagerResponse response = new DeleteDeliveryManagerResponse(result.deliveryManagerId());

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    private boolean hasAdminOrHubRole(String role) {
        return "ADMIN".equals(role) || "HUB".equals(role);
    }

    private <T> ResponseEntity<CommonResponse<T>> forbiddenResponse() {
        // CommonResponse에 fail 추가 후 수정 예정
        return ResponseEntity.ok(CommonResponse.success("접근 권한이 없습니다.", null));
//            return ResponseEntity
//                    .status(HttpStatus.FORBIDDEN)
//                    .body(CommonResponse.fail(403, "접근 권한이 없습니다."));
    }
}
