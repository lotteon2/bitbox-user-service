package com.bitbox.user.controller;

import com.bitbox.user.domain.Attendance;
import com.bitbox.user.dto.AttendanceUpdateDto;
import com.bitbox.user.dto.CurrentLocationDto;
import com.bitbox.user.service.AttendanceService;
import com.bitbox.user.service.response.AvgAttendanceInfo;
import com.bitbox.user.service.response.MemberInfoWithAttendance;
import io.github.bitbox.bitbox.enums.AttendanceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    /**
     * 내 출결 조회
     */
    @GetMapping("/mypage/attendance")
    public ResponseEntity<List<Attendance>> getAllMyAttendance(@RequestHeader String memberId) {
        return ResponseEntity.ok(attendanceService.getAllMyAttendance(memberId));
    }

    /**
     * 출결 조회 대시보드용(관리자)
     */
    @GetMapping("/admin/attendance/dashboard/{classId}")
    public ResponseEntity<List<AvgAttendanceInfo>> getAllAttendanceForDashboard(@PathVariable Long classId) {
        return ResponseEntity.ok(attendanceService.getAttendanceForDashboard(classId));
    }

    /**
     * 출결 리스트 조회(관리자)
     */
    @GetMapping("/admin/attendance/{classId}")
    public ResponseEntity<List<MemberInfoWithAttendance>> getAllAttendance(@PathVariable Long classId, @RequestParam(required = false) String current, @RequestParam(required = false) String memberName) {

        return ResponseEntity.ok(attendanceService.getAttendanceForAdmin(classId, current, memberName == null ? memberName : URLDecoder.decode(memberName, StandardCharsets.UTF_8)));
    }

    /**
     * 출결 수정(관리자)
     */
    @PatchMapping("/admin/attendance")
    public ResponseEntity<AttendanceStatus> updateAttendanceState(@Valid @RequestBody AttendanceUpdateDto attendanceUpdateDto) {
        return ResponseEntity.ok(attendanceService.updateAttendanceState(attendanceUpdateDto));
    }

    /**
     * 입실 체크
     */
    @PatchMapping("/mypage/attendance/entrance")
    public ResponseEntity<AttendanceStatus> memberEntrance(@RequestHeader String memberId, @Valid @RequestBody CurrentLocationDto currentLocationDto) {
        return ResponseEntity.ok(attendanceService.memberEntrance(memberId, currentLocationDto));
    }

    /**
     * 퇴실 체크
     */
    @PatchMapping("/mypage/attendance/quit")
    public ResponseEntity<AttendanceStatus> memberQuit(@RequestHeader String memberId, @Valid @RequestBody CurrentLocationDto currentLocationDto) {
        attendanceService.memberQuit(memberId, currentLocationDto);
        return ResponseEntity.ok().build();
    }
}
