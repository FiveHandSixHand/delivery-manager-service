package com.fhsh.daitda.deliverymanager.presentation.controller;

import com.fhsh.daitda.deliverymanager.application.command.CreateDeliveryManagerCommand;
import com.fhsh.daitda.deliverymanager.application.result.CreateDeliveryManagerResult;
import com.fhsh.daitda.deliverymanager.application.service.command.DeliveryManagerCommandService;
import com.fhsh.daitda.deliverymanager.presentation.dto.request.CreateDeliveryManagerRequest;
import com.fhsh.daitda.deliverymanager.presentation.dto.response.CreateDeliveryManagerResponse;
import com.fhsh.daitda.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery-managers")
@RestController
public class DeliveryManagerController {

    private final DeliveryManagerCommandService service;

    //@PreAuthorize("hasAnyRole('ADMIN', 'HUB')")
    @PostMapping
    public ResponseEntity<CommonResponse> createDeliveryManager(
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestBody CreateDeliveryManagerRequest request) {

        if (!(role.equals("ADMIN") || role.equals("HUB"))) {
            // CommonResponse에 fail 추가 후 수정 예정
            return ResponseEntity.ok(CommonResponse.success("접근 권한이 없습니다."));
//            return ResponseEntity
//                    .status(HttpStatus.FORBIDDEN)
//                    .body(CommonResponse.fail(403, "접근 권한이 없습니다."));
        }

        // Role이 HUB인 경우, 소속 hubId 확인 필요

        CreateDeliveryManagerCommand command = new CreateDeliveryManagerCommand(request.targetUserId(), request.type());

        CreateDeliveryManagerResult result = service.createDeliveryManager(command);

        CreateDeliveryManagerResponse response = new CreateDeliveryManagerResponse(result.deliveryManagerId());

        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
