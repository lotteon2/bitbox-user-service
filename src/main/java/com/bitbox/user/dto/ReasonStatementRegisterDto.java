package com.bitbox.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReasonStatementRegisterDto {
    private Long attendanceId;
    private String reasonTitle;
    private String reasonContent;
    private String reasonAttachedFile;
}
