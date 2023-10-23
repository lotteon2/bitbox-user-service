package com.bitbox.user.service.response;

import com.querydsl.core.annotations.QueryProjection;
import io.github.bitbox.bitbox.enums.AttendanceStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Builder
@Getter
@ToString
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

    @Builder
    @QueryProjection
    public MemberInfoWithAttendance(String memberId, String memberProfileImg, String memberNickname, Long attendanceId, LocalDate attendanceDate, String entrace, String quit, AttendanceStatus attendanceState, String attendanceModifyReason, String reasonTitle) {
        this.memberId = memberId;
        this.memberProfileImg = memberProfileImg;
        this.memberNickname = memberNickname;
        this.attendanceId = attendanceId;
        this.attendanceDate = attendanceDate;
        this.entrace = entrace;
        this.quit = quit;
        this.attendanceState = attendanceState;
        this.attendanceModifyReason = attendanceModifyReason;
        this.reasonTitle = reasonTitle;
    }
}
