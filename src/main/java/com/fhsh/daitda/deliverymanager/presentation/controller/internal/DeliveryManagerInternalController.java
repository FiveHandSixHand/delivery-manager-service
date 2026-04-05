package com.fhsh.daitda.deliverymanager.presentation.controller.internal;

import com.fhsh.daitda.deliverymanager.application.command.CompleteAssignmentCommand;
import com.fhsh.daitda.deliverymanager.application.result.CompleteAssignmentResult;
import com.fhsh.daitda.deliverymanager.application.service.command.DeliveryManagerCommandService;
import com.fhsh.daitda.deliverymanager.presentation.dto.request.CompleteAssignmentRequest;
import com.fhsh.daitda.deliverymanager.presentation.dto.response.CompleteAssignmentResponse;
import com.fhsh.daitda.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/delivery-managers")
public class DeliveryManagerInternalController {

    private final DeliveryManagerCommandService commandService;

    @PostMapping("/assignments")
    public ResponseEntity<CommonResponse<CompleteAssignmentResponse>> completeAssignment(
            @RequestHeader(value = "X-User-Role") String role,
            @RequestBody CompleteAssignmentRequest request
    ) {

        if (!("ADMIN".equals(role) || "HUB_ADMIN".equals(role))) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(CommonResponse.fail(403, "접근 권한이 없습니다.", null));
        }


        CompleteAssignmentCommand command = new CompleteAssignmentCommand(request.deliveryId(), request.hubId());
        CompleteAssignmentResult result = commandService.completeAssignment(command);
        CompleteAssignmentResponse response = CompleteAssignmentResponse.from(result);

        return ResponseEntity.ok(CommonResponse.success("배송담당자 배정이 완료되었습니다.", response));
    }
}
