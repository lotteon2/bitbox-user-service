package com.bitbox.user.util;

import com.bitbox.user.domain.Attendance;
import com.bitbox.user.domain.Member;
import com.bitbox.user.dto.CurrentLocationDto;
import com.bitbox.user.exception.InvalidAttendanceRequestException;
import com.bitbox.user.exception.InvalidRangeAttendanceException;
import io.github.bitbox.bitbox.enums.AttendanceStatus;

import java.time.LocalTime;

public class AttendanceUtil {
    public static final double BIT_CENTER_LAT = 37.4946;
    public static final double BIT_CENTER_LNG = 127.0276;

    public static final LocalTime BEFORE_ATTENDANCE_TIME_ENTRACE = LocalTime.of(7, 0, 0);
    public static final LocalTime ATTENDANCE_TIME_ENTRACE = LocalTime.of(9, 0,0);
    public static final LocalTime ATTENDANCE_TIME_VALID = LocalTime.of(14, 0, 0);
    public static final LocalTime ATTENDANCE_TIME_QUIT = LocalTime.of(18, 0, 0);
    public static final LocalTime AFTER_ATTENDANCE_TIME_QUIT = LocalTime.of(22, 30, 0);

    public static void checkLocation(double lat, double lng) {
        if (Math.pow(0.005, 2) <= Double.parseDouble(String.format("%.8f", (Math.pow(BIT_CENTER_LAT - lat, 2) + Math.pow(BIT_CENTER_LNG - lng, 2))))) {
            throw new InvalidRangeAttendanceException("교육장과 멀리 떨어져 있습니다.");
        }
    }

    public static LocalTime getCurrent(CurrentLocationDto currentLocationDto){
        LocalTime current;

        if (currentLocationDto.getCurrent() == null) current = LocalTime.now();
        else current = LocalTime.of(Integer.parseInt(currentLocationDto.getCurrent().split(":")[0]), Integer.parseInt(currentLocationDto.getCurrent().split(":")[1]), Integer.parseInt(currentLocationDto.getCurrent().split(":")[2]));


        return current;
    }

    public static AttendanceStatus checkEntraceTime(String memberId, Member memberInfo, LocalTime current, Attendance attendance) {
        // 7시 이전 입실 요청 | 14시 이후 입실 요청 | 본인의 출결이 아닌 경우 => 예외
        if (!(memberInfo.getMemberId().equals(memberId)) || current.isBefore(AttendanceUtil.BEFORE_ATTENDANCE_TIME_ENTRACE) || current.isAfter(AttendanceUtil.ATTENDANCE_TIME_VALID)) throw new InvalidAttendanceRequestException("유효하지 않은 출결 요청입니다.");
        // 입실은 유효한 처음 요청만 저장
        if (attendance.getAttendanceState() != AttendanceStatus.ABSENT) return attendance.getAttendanceState();
        // 7시 ~ 9시 입실 => 출석
        if (current.isAfter(AttendanceUtil.BEFORE_ATTENDANCE_TIME_ENTRACE) && current.isBefore(AttendanceUtil.ATTENDANCE_TIME_ENTRACE)) {
            attendance.setAttendanceState(AttendanceStatus.ATTENDANCE);
        }
        // 9시 ~ 14시 입실 => 지각
        else if (current.isAfter(AttendanceUtil.ATTENDANCE_TIME_ENTRACE) && current.isBefore(AttendanceUtil.ATTENDANCE_TIME_VALID)) {
            attendance.setAttendanceState(AttendanceStatus.TARDY);
        }

        attendance.setEntraceTime(current);
        return attendance.getAttendanceState();
    }

    public static AttendanceStatus checkQuitTime(String memberId, Member memberInfo, LocalTime current, Attendance attendance) {
        // 14시 이전 퇴실 요청 | 22시 이후 퇴실 요청 | 본인의 출결이 아닌 경우 => 예외
        if (!(memberInfo.getMemberId().equals(memberId)) || current.isBefore(AttendanceUtil.ATTENDANCE_TIME_VALID) || current.isAfter(AttendanceUtil.AFTER_ATTENDANCE_TIME_QUIT)) throw new InvalidAttendanceRequestException("유효하지 않은 출결 요청입니다.");

        // 현재 출결상태인 경우
        if (attendance.getAttendanceState() == AttendanceStatus.ATTENDANCE) {
            // 14시 ~ 18시 퇴실 => 조퇴
            if (current.isAfter(AttendanceUtil.ATTENDANCE_TIME_VALID) && current.isBefore(AttendanceUtil.ATTENDANCE_TIME_QUIT)) {
                attendance.setAttendanceState(AttendanceStatus.LEAVE_EARLY);
            }
            // 18시 ~ 22시 퇴실 => 출석 유지
        }
        // 현재 조퇴 상태인 경우
        if (attendance.getAttendanceState() == AttendanceStatus.LEAVE_EARLY) {
            // 18시 ~ 22시 퇴실 => 출석
            if (current.isAfter(AttendanceUtil.ATTENDANCE_TIME_QUIT)) {
                attendance.setAttendanceState(AttendanceStatus.ATTENDANCE);
            }
        }
        // 예외 시간 이외의 퇴실 요청에 대해서 현재 지각 | 결석 상태인 경우 이전 상태 그대로 유지
        attendance.setQuitTime(current);

        return attendance.getAttendanceState();
    }
}
