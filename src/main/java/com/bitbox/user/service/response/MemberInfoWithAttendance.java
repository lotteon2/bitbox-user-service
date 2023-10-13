package com.bitbox.user.service.response;

import io.github.bitbox.bitbox.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
public class MemberInfoWithAttendance {
    private String memberId;
    private String memberProfileImg;
    private String memberNickname;
    private Long attendanceId;
    private LocalDate attendanceDate;
    private String entrace;
    private String quit;
    private AttendanceStatus attendanceState;
    private String attendanceModifyReason;
    private String reasonTitle;

}
