package com.bixbox.user.exception.advice;

import com.bixbox.user.exception.DuplicationEmailException;
import com.bixbox.user.exception.InvalidMemberIdException;
import com.bixbox.user.exception.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiControllerAdvice {
    /**
     * 중복 이메일 에러
     * @param e
     * @return ErrorResponse
     */
    @ExceptionHandler(DuplicationEmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDuplicationEmailException(DuplicationEmailException e) {
        return getErrorResponse(e);
    }

    /**
     * 회원정보 조회 에러
     * @param e
     * @return ErrorResponse
     */
    @ExceptionHandler(InvalidMemberIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidMemberIdException(InvalidMemberIdException e) {
        return getErrorResponse(e);
    }

    private ErrorResponse getErrorResponse(RuntimeException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }
}
