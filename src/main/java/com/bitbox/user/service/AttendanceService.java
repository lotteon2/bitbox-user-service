package com.bitbox.user.service;

import com.bitbox.user.domain.Attendance;
import com.bitbox.user.domain.Member;
import com.bitbox.user.dto.AttendanceUpdateDto;
import com.bitbox.user.dto.CurrentLocationDto;
import com.bitbox.user.exception.InvalidAttendanceException;
import com.bitbox.user.exception.InvalidMemberIdException;
import com.bitbox.user.exception.InvalidRangeAttendanceException;
import com.bitbox.user.repository.AttendanceRepository;
import com.bitbox.user.repository.MemberInfoRepository;
import io.github.bitbox.bitbox.enums.AttendanceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final MemberInfoRepository memberInfoRepository;

    private final double BIT_CENTER_LAT = 37.4946;
    private final double BIT_CENTER_LNG = 127.0276;

    private final LocalTime BEFORE_ATTENDANCE_TIME_ENTRACE = LocalTime.of(7, 0, 0);
    private final LocalTime ATTENDANCE_TIME_ENTRACE = LocalTime.of(9, 0,0);
    private final LocalTime ATTENDANCE_TIME_VALID = LocalTime.of(14, 0, 0);
    private final LocalTime ATTENDANCE_TIME_QUIT = LocalTime.of(18, 0, 0);

    private Attendance getAttendance(Long attendanceId, CurrentLocationDto currentLocationDto) {
        Attendance attendance = attendanceRepository.findById(attendanceId).orElseThrow(() -> new InvalidAttendanceException("존재하지 않는 출결정보입니다."));
        double lat = currentLocationDto.getLat();
        double lng = currentLocationDto.getLng();

        // 반경 100M 이내
        if (Math.pow(0.001, 2) <= (Math.pow(BIT_CENTER_LAT - lat, 2) + Math.pow(BIT_CENTER_LNG - lng, 2))) {
            throw new InvalidRangeAttendanceException("교육장과 멀리 떨어져 있습니다.");
        }
        return attendance;
    }


    /**
     * 입실 체크
     * @param attendanceId
     * @param currentLocationDto
     */
    @Transactional
    public AttendanceStatus memberEntrance(Long attendanceId, CurrentLocationDto currentLocationDto) {
        LocalTime current;
        if (currentLocationDto.getCurrent() == null) current = LocalTime.now();
        else current = LocalTime.of(Integer.parseInt(currentLocationDto.getCurrent().split(":")[0]), Integer.parseInt(currentLocationDto.getCurrent().split(":")[1]), Integer.parseInt(currentLocationDto.getCurrent().split(":")[2]));

        Attendance attendance = getAttendance(attendanceId, currentLocationDto);
        attendance.setEntraceTime(current);

        if ((current.isAfter(BEFORE_ATTENDANCE_TIME_ENTRACE) && current.isBefore(ATTENDANCE_TIME_ENTRACE)) || current.equals(ATTENDANCE_TIME_ENTRACE)) { // 출석
            attendance.setAttendanceState(AttendanceStatus.ATTENDANCE);
        } else if(current.isAfter(ATTENDANCE_TIME_ENTRACE)) { // 입실 시간보다 늦게 입실체크한 경우 지각
            attendance.setAttendanceState(AttendanceStatus.TARDY);
        } else if(current.isAfter(ATTENDANCE_TIME_VALID)) {

        }

        return attendance.getAttendanceState();
    }

    /**
     * 퇴실 체크
     * @param attendanceId
     * @param currentLocationDto
     */
    @Transactional
    public void memberQuit(Long attendanceId, CurrentLocationDto currentLocationDto) {
        LocalTime current;
        if (currentLocationDto.getCurrent() == null) current = LocalTime.now();
        else current = LocalTime.of(Integer.parseInt(currentLocationDto.getCurrent().split(":")[0]), Integer.parseInt(currentLocationDto.getCurrent().split(":")[1]), Integer.parseInt(currentLocationDto.getCurrent().split(":")[2]));

        Attendance attendance = getAttendance(attendanceId, currentLocationDto);
        attendance.setQuitTime(current);

        if (attendance.getAttendanceState() == AttendanceStatus.ATTENDANCE && current.isBefore(ATTENDANCE_TIME_QUIT)) { // 현재 출석 상태에서 퇴실 시간보다 일찍 퇴실체크한 경우 조퇴
            attendance.setAttendanceState(AttendanceStatus.GO_OUT);
        }
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
     * 클래스별 출결 조회(관리자)
     * @param classId
     * @return
     */
    public List<Attendance> getAttendanceForAdmin(long classId) {
        return attendanceRepository.findByClassIdForAdmin(classId, LocalDate.now().minusDays(6), LocalDate.now());
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
}
