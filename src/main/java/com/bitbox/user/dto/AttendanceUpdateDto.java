package com.bitbox.user.dto;

import com.bitbox.user.domain.Attendance;
import io.github.bitbox.bitbox.enums.AttendanceStatus;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Builder
@Getter
public class AttendanceUpdateDto {
    @NotNull(message = "필수 입력값입니다")
    private Long attendanceId;
    @NotNull(message = "필수 입력값입니다")
    private AttendanceStatus attendanceState;
    private String attendanceModifyReason;
}
