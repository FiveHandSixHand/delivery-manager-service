package com.fhsh.daitda.deliverymanager.presentation.controller;

import com.fhsh.daitda.deliverymanager.application.command.CreateDeliveryManagerCommand;
import com.fhsh.daitda.deliverymanager.application.result.CreateDeliveryManagerResult;
import com.fhsh.daitda.deliverymanager.application.service.command.DeliveryManagerCommandService;
import com.fhsh.daitda.deliverymanager.presentation.dto.request.CreateDeliveryManagerRequest;
import com.fhsh.daitda.deliverymanager.presentation.dto.response.CreateDeliveryManagerResponse;
import com.fhsh.daitda.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery-managers")
@RestController
public class DeliveryManagerController {

    private final DeliveryManagerCommandService service;

    @PostMapping
    public ResponseEntity<CommonResponse> createDeliveryManager(@RequestBody CreateDeliveryManagerRequest request) {
        CreateDeliveryManagerCommand command = new CreateDeliveryManagerCommand(request.targetUserId(), request.type());

        CreateDeliveryManagerResult result = service.createDeliveryManager(command);

        CreateDeliveryManagerResponse response = new CreateDeliveryManagerResponse(result.deliveryManagerId());

        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
