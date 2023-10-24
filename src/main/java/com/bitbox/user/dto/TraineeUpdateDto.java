package com.bitbox.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TraineeUpdateDto {
    private String name;
    private Long classId;
}
