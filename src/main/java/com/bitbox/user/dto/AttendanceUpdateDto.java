package com.bitbox.user.dto;

import com.bitbox.user.domain.Attendance;
import io.github.bitbox.bitbox.enums.AttendanceStatus;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@Builder
@Getter
public class AttendanceUpdateDto {
    @NotEmpty(message = "필수 입력값입니다")
    private Long attendanceId;
    @NotEmpty(message = "필수 입력값입니다")
    private String memberId;
    @NotEmpty(message = "필수 입력값입니다")
    private AttendanceStatus attendanceStatus;
    private String attendancceModifyReason;
}
