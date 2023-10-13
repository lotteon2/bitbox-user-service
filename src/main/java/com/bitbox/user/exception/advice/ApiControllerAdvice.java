package com.bitbox.user.exception.advice;

import com.bitbox.user.exception.*;
import com.bitbox.user.exception.response.ErrorResponse;
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

    /**
     * 출결정보 조회 에러
     * @param e
     * @return ErrorResponse
     */
    @ExceptionHandler(InvalidAttendanceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidAttendanceIdException(InvalidAttendanceException e) {
        return getErrorResponse(e);
    }

    /**
     * 유효하지 않은 위치 정보 에러
     * @param e
     * @return ErrorResponse
     */
    @ExceptionHandler(InvalidRangeAttendanceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidRangeAttendanceException(InvalidRangeAttendanceException e) {
        return getErrorResponse(e);
    }

    /**
     * 부정 출결
     * @param e
     * @return
     */
    @ExceptionHandler(InvalidAttendanceRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidAttendanceRequestException(InvalidAttendanceRequestException e) {
        return getErrorResponse(e);
    }

    /**
     * 크레딧 부족
     * @param e
     * @return
     */
    @ExceptionHandler(InSufficientCreditException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInSufficientCreditException(InSufficientCreditException e) {
        return getErrorResponse(e);
    }
    private ErrorResponse getErrorResponse(RuntimeException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }



}
