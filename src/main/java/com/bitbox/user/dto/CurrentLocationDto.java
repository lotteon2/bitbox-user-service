package com.bitbox.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CurrentLocationDto {
    @NotNull(message = "필수 입력값입니다")
    private Double lat;
    @NotNull(message = "필수 입력값입니다")
    private Double lng;
    private String current;
}
