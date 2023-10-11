package com.bitbox.user.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@Builder
@Getter
public class CurrentLocationDto {
    @NotEmpty(message = "필수 입력값입니다")
    private double lat;
    @NotEmpty(message = "필수 입력값입니다")
    private double lng;
}
