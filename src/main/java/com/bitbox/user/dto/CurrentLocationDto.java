package com.bitbox.user.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CurrentLocationDto {
    private long lat;
    private long lng;
}
