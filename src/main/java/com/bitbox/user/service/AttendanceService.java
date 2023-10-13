package com.bitbox.user.service;

import com.bitbox.user.domain.Attendance;
import com.bitbox.user.domain.Member;
import com.bitbox.user.dto.AttendanceUpdateDto;
import com.bitbox.user.dto.CurrentLocationDto;
import com.bitbox.user.exception.InvalidAttendanceException;
import com.bitbox.user.exception.InvalidAttendanceRequestException;
import com.bitbox.user.exception.InvalidMemberIdException;
import com.bitbox.user.repository.AttendanceRepository;
import com.bitbox.user.repository.MemberInfoRepository;
import com.bitbox.user.service.response.AvgAttendanceInfo;
import com.bitbox.user.service.response.MemberInfoWithAttendance;
import com.bitbox.user.util.AttendanceUtil;
import io.github.bitbox.bitbox.enums.AttendanceStatus;
import io.github.bitbox.bitbox.enums.AuthorityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final MemberInfoRepository memberInfoRepository;

    /**
     * 입실 체크
     * @param attendanceId
     * @param currentLocationDto
     */
    @Transactional
    public AttendanceStatus memberEntrance(String memberId, Long attendanceId, CurrentLocationDto currentLocationDto) {
        LocalTime current = AttendanceUtil.getCurrent(currentLocationDto);
        Attendance attendance = getAttendance(attendanceId, currentLocationDto);
        Member memberInfo = attendance.getMember();

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

    /**
     * 퇴실 체크
     * @param attendanceId
     * @param currentLocationDto
     */
    @Transactional
    public AttendanceStatus memberQuit(String memberId, Long attendanceId, CurrentLocationDto currentLocationDto) {
        LocalTime current = AttendanceUtil.getCurrent(currentLocationDto);
        Attendance attendance = getAttendance(attendanceId, currentLocationDto);
        Member memberInfo = attendance.getMember();

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
        // 예외 시간 이외의 퇴실 요청에 대해서 현재 지각 | 결석 상태인 경우 이전 상태 그대로 유지
        attendance.setQuitTime(current);

        return attendance.getAttendanceState();
    }

    /**
     * 내 출결 조회
     * @param memberId
     * @return
     */
    public List<Attendance> getAllMyAttendance(String memberId) {
        memberInfoRepository.findByMemberIdAndDeletedIsFalse(memberId).orElseThrow(() -> new InvalidMemberIdException("존재하지 않거나 유효하지 않은 회원정보입니다."));
        return attendanceRepository.findByMemberId(memberId);
    }

    /**
     * 오늘 포함 7일에 대한 특정 반의 날짜별 평균 정보 조회
     * @param classId
     * @return
     */
    public List<AvgAttendanceInfo> getAttendanceForDashboard(long classId) {
        return attendanceRepository.findByClassIdForAdminDashBoard(classId, LocalDate.now().minusDays(6), LocalDate.now());
    }

    /**
     * 클래스별 출결 정보 조회(관리자)
     * @param classId
     * @return
     */
    public List<MemberInfoWithAttendance> getAttendanceForAdmin(long classId) {
        return attendanceRepository.findByClassIdForAdmin(classId);
    }

    /**
     * 출결 상태 변경
     * @param attendanceUpdateDto
     * @return
     */
    @Transactional
    public AttendanceStatus updateAttendanceState(AttendanceUpdateDto attendanceUpdateDto) {
        Attendance attendance = attendanceRepository.findByAttendanceId(attendanceUpdateDto.getAttendanceId());

        attendance.setAttendanceState(attendanceUpdateDto.getAttendanceState());
        attendance.setAttendanceModifyReason(attendanceUpdateDto.getAttendanceModifyReason());

        return attendanceRepository.save(attendance).getAttendanceState();
    }

    private Attendance getAttendance(Long attendanceId, CurrentLocationDto currentLocationDto) {
        Attendance attendance = attendanceRepository.findById(attendanceId).orElseThrow(() -> new InvalidAttendanceException("존재하지 않는 출결정보입니다."));

        // 반경 100M 이내
        AttendanceUtil.checkLocation(currentLocationDto.getLat(),currentLocationDto.getLng());

        return attendance;
    }
}
