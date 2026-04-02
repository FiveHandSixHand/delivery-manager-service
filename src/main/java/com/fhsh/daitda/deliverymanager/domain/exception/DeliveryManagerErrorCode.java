package com.fhsh.daitda.deliverymanager.domain.exception;

import com.fhsh.daitda.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DeliveryManagerErrorCode implements ErrorCode {
    DELETE_ME(HttpStatus.CONFLICT, "작성을 위한 예시입니다.");

    private final HttpStatus status;
    private final String description;
}
