package com.bitbox.user.service;

import com.bitbox.user.domain.Attendance;
import com.bitbox.user.domain.Member;
import com.bitbox.user.dto.AttendanceUpdateDto;
import com.bitbox.user.dto.CurrentLocationDto;
import com.bitbox.user.exception.InvalidAttendanceException;
import com.bitbox.user.exception.InvalidMemberIdException;
import com.bitbox.user.repository.AttendanceRepository;
import com.bitbox.user.repository.MemberInfoRepository;
import com.bitbox.user.service.response.AvgAttendanceInfo;
import com.bitbox.user.service.response.MemberInfoWithAttendance;
import com.bitbox.user.util.AttendanceUtil;
import io.github.bitbox.bitbox.enums.AttendanceStatus;
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

        return AttendanceUtil.checkEntraceTime(memberId, memberInfo, current, attendance);
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

        return AttendanceUtil.checkQuitTime(memberId, memberInfo, current, attendance);
    }

    /**
     * 내 출결 조회
     * @param memberId
     * @return
     */
    public List<Attendance> getAllMyAttendance(String memberId) {
        return memberInfoRepository.findByMemberIdAndDeletedIsFalse(memberId).orElseThrow(() -> new InvalidMemberIdException("존재하지 않거나 유효하지 않은 회원정보입니다.")).getAttendances();
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
    public List<MemberInfoWithAttendance> getAttendanceForAdmin(long classId, LocalDate current, String memberNickname) {
        return attendanceRepository.findByClassIdForAdmin(classId, current, memberNickname);
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
