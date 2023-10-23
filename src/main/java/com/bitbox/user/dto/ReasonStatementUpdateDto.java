package com.bitbox.user.dto;

import io.github.bitbox.bitbox.enums.ReasonStatementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReasonStatementUpdateDto {
    private ReasonStatementStatus reasonState;
    private String rejectReason;
}
