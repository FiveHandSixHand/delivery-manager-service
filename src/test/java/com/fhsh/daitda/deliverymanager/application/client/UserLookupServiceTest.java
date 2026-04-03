package com.fhsh.daitda.deliverymanager.application.client;

import com.fhsh.daitda.deliverymanager.application.command.UserInfoCommand;
import com.fhsh.daitda.deliverymanager.domain.exception.DeliveryManagerErrorCode;
import com.fhsh.daitda.deliverymanager.infrastructure.external.UserFeignClient;
import com.fhsh.daitda.deliverymanager.infrastructure.external.dto.UserResponse;
import com.fhsh.daitda.exception.BusinessException;
import com.fhsh.daitda.response.CommonResponse;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserLookupServiceTest {

    @InjectMocks
    private UserLookupService userLookupService;

    @Mock
    private UserFeignClient userFeignClient;

    @Test
    @DisplayName("정상 케이스 - 사용자 조회 성공 시 UserInfoCommand로 변환")
    void getUser_success() {
        // given
        UUID userId = UUID.randomUUID();
        UUID hubId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // 조회된 응답 설정
        UserResponse userResponse = new UserResponse(
                userId,
                "test@example.com",
                "가나다",
                "MASTER",
                "APPROVED",
                "exampleId",
                hubId,
                companyId,
                now,
                now
        );
        CommonResponse<UserResponse> response = CommonResponse.success(userResponse);

        given(userFeignClient.getUser(userId)).willReturn(response);

        // when
        UserInfoCommand result = userLookupService.getUser(userId);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.hubId()).isEqualTo(hubId);
        assertThat(result.slackUserId()).isEqualTo("exampleId");
    }

    @Test
    @DisplayName("실패 케이스 - 응답 data가 null이면 USER_NOT_FOUND 예외 발생")
    void getUser_fail_whenDataIsNull() {
        // given
        UUID userId = UUID.randomUUID();
        CommonResponse<UserResponse> response = CommonResponse.success(null); // 조회 결과 없는 경우

        given(userFeignClient.getUser(userId)).willReturn(response);

        // when / then
        assertThatThrownBy(() -> userLookupService.getUser(userId))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(DeliveryManagerErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 케이스 - fallback에서 NotFound면 USER_NOT_FOUND 예외 발생")
    void fallbackGetUser_notFound() {
        // given
        UUID userId = UUID.randomUUID();
        // GET 요청이므로 body를 비우고, 테스트용이므로 헤더도 emptyMap()으로 요청
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/internal/v1/users/" + userId,
                Collections.emptyMap(),
                null,
                StandardCharsets.UTF_8,
                new RequestTemplate() // Feign이 내부적으로 사용하는 요청 템플릿
        );
        FeignException.NotFound exception =
                new FeignException.NotFound("not found", request, null, Collections.emptyMap());

        // when / then
        assertThatThrownBy(() -> userLookupService.fallbackGetUser(userId, exception))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(DeliveryManagerErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 케이스 - fallback에서 기타 예외면 USER_SERVICE_ERROR 예외 발생")
    void fallbackGetUser_serviceError() {
        // given
        UUID userId = UUID.randomUUID();

        // when / then
        assertThatThrownBy(() -> userLookupService.fallbackGetUser(userId, new RuntimeException("timeout")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(DeliveryManagerErrorCode.USER_SERVICE_ERROR);
    }
}
