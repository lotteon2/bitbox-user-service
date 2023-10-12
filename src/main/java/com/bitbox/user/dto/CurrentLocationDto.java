package com.bitbox.user.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Builder
@Getter
public class CurrentLocationDto {
    @NotNull(message = "필수 입력값입니다")
    private Double lat;
    @NotNull(message = "필수 입력값입니다")
    private Double lng;
    private String current;
}
