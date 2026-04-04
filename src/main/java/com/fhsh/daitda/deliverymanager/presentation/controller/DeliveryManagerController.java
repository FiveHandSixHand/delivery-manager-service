package com.fhsh.daitda.deliverymanager.presentation.controller;

import com.fhsh.daitda.deliverymanager.application.command.CreateDeliveryManagerCommand;
import com.fhsh.daitda.deliverymanager.application.query.GetDeliveryManagerListQuery;
import com.fhsh.daitda.deliverymanager.application.result.CreateDeliveryManagerResult;
import com.fhsh.daitda.deliverymanager.application.result.DeleteDeliveryManagerResult;
import com.fhsh.daitda.deliverymanager.application.result.GetDeliveryManagerListResult;
import com.fhsh.daitda.deliverymanager.application.service.command.DeliveryManagerCommandService;
import com.fhsh.daitda.deliverymanager.application.service.query.DeliveryManagerQueryService;
import com.fhsh.daitda.deliverymanager.presentation.dto.request.CreateDeliveryManagerRequest;
import com.fhsh.daitda.deliverymanager.presentation.dto.request.GetDeliveryManagerListRequest;
import com.fhsh.daitda.deliverymanager.presentation.dto.response.CreateDeliveryManagerResponse;
import com.fhsh.daitda.deliverymanager.presentation.dto.response.DeleteDeliveryManagerResponse;
import com.fhsh.daitda.deliverymanager.presentation.dto.response.GetDeliveryManagerListResponse;
import com.fhsh.daitda.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery-managers")
@RestController
public class DeliveryManagerController {

    private final DeliveryManagerCommandService commandService;
    private final DeliveryManagerQueryService queryService;

    // 배송담당자 생성
    @PostMapping
    public ResponseEntity<CommonResponse<CreateDeliveryManagerResponse>> createDeliveryManager(
            @RequestHeader(value = "X-User-Role") String role,
            @RequestBody CreateDeliveryManagerRequest request) {

        if (!hasAdminOrHubRole(role)) {
            return forbiddenResponse();
        }

        // Role이 HUB인 경우, 소속 hubId 확인 필요

        CreateDeliveryManagerCommand command = new CreateDeliveryManagerCommand(request.targetUserId(), request.type());
        CreateDeliveryManagerResult result = commandService.createDeliveryManager(command);
        CreateDeliveryManagerResponse response = new CreateDeliveryManagerResponse(result.deliveryManagerId());

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    // 배송담당자 삭제
    @DeleteMapping("/{deliveryManagerId}")
    public ResponseEntity<CommonResponse<DeleteDeliveryManagerResponse>> deleteDeliveryManager(
            @RequestHeader(value = "X-User-Id") String userId,
            @RequestHeader(value = "X-User-Role") String role,
            @PathVariable UUID deliveryManagerId) {

        if (!hasAdminOrHubRole(role)) {
            return forbiddenResponse();
        }

        DeleteDeliveryManagerResult result = commandService.deleteDeliveryManager(userId, deliveryManagerId);
        DeleteDeliveryManagerResponse response = new DeleteDeliveryManagerResponse(result.deliveryManagerId());

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    // 배송담당자 목록 조회
    @GetMapping
    public ResponseEntity<CommonResponse<Page<GetDeliveryManagerListResponse>>> getDeliveryManagers(
            @RequestHeader(value = "X-User-Role") String role,
            @RequestParam(defaultValue = "0") int page,
            @ModelAttribute GetDeliveryManagerListRequest request
    ) {
        if (!hasAdminOrHubRole(role)) {
            return forbiddenResponse();
        }

        Sort sort = getSort(request.getSortBy());
        Pageable pageable = PageRequest.of(page, 10, sort);

        GetDeliveryManagerListQuery query = GetDeliveryManagerListQuery.from(request);

        Page<GetDeliveryManagerListResponse> response = queryService
                .getDeliveryManagers(query, pageable)
                .map(GetDeliveryManagerListResponse::from);

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    // 권한 체크
    private boolean hasAdminOrHubRole(String role) {
        return "ADMIN".equals(role) || "HUB".equals(role);
    }

    // 권한 검증 실패 시 응답
    private <T> ResponseEntity<CommonResponse<T>> forbiddenResponse() {
        // CommonResponse에 fail 추가 후 수정 예정
        return ResponseEntity.ok(CommonResponse.success("접근 권한이 없습니다.", null));
//            return ResponseEntity
//                    .status(HttpStatus.FORBIDDEN)
//                    .body(CommonResponse.fail(403, "접근 권한이 없습니다."));
    }

    // 생성일/수정일 순 정렬
    private Sort getSort(String sortBy) {
        if ("updatedAt".equalsIgnoreCase(sortBy)) {
            return Sort.by(Sort.Direction.ASC, "updatedAt")
                    .and(Sort.by(Sort.Direction.ASC, "createdAt"));
        }

        return Sort.by(Sort.Direction.ASC, "createdAt")
                .and(Sort.by(Sort.Direction.ASC, "updatedAt"));
    }
}
