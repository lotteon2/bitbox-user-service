package com.bitbox.user.service.response;

import io.github.bitbox.bitbox.enums.ReasonStatementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReasonStatementWithAttendanceAndMember {
    private Long reasonStatementId;
    private LocalDate attendanceDate;
    private String memberName;
    private String reasonTitle;
    private String reasonContent;
    private String reasonAttachedFile;
    private ReasonStatementStatus reasonState;
    private boolean isRead;
}
