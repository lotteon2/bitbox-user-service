package com.bitbox.user.controller;

import com.bitbox.user.domain.Attendance;
import com.bitbox.user.dto.AttendanceUpdateDto;
import com.bitbox.user.dto.CurrentLocationDto;
import com.bitbox.user.service.AttendanceService;
import com.bitbox.user.service.response.AvgAttendanceInfo;
import com.bitbox.user.service.response.MemberInfoWithAttendance;
import io.github.bitbox.bitbox.enums.AttendanceStatus;
import io.github.bitbox.bitbox.jwt.JwtPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.PathParam;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AttendanceController {
    private final AttendanceService attendanceService;

    @GetMapping("/mypage/attendance")
    public ResponseEntity<List<Attendance>> getAllMyAttendance(@RequestHeader JwtPayload payload) {
        return ResponseEntity.ok(attendanceService.getAllMyAttendance(payload.getMemberId()));
    }

    @GetMapping("/admin/attendance/dashboard/{classId}")
    public ResponseEntity<List<AvgAttendanceInfo>> getAllAttendanceForDashboard(@PathVariable Long classId) {
        return ResponseEntity.ok(attendanceService.getAttendanceForDashboard(classId));
    }

    @GetMapping("/admin/attendance/{classId}")
    public ResponseEntity<List<MemberInfoWithAttendance>> getAllAttendance(@PathVariable Long classId, @RequestParam(required = false) LocalDate current, @RequestParam(required = false) String memberNickname) {
        return ResponseEntity.ok(attendanceService.getAttendanceForAdmin(classId, current, memberNickname));
    }

    @PatchMapping("/admin/attendance")
    public ResponseEntity<AttendanceStatus> updateAttendanceState(@Valid @RequestBody AttendanceUpdateDto attendanceUpdateDto) {
        return ResponseEntity.ok(attendanceService.updateAttendanceState(attendanceUpdateDto));
    }

    @PatchMapping("/mypage/attendance/entrance/{attendanceId}")
    public ResponseEntity<AttendanceStatus> memberEntrance(@RequestHeader JwtPayload payload, @PathVariable Long attendanceId, @Valid @RequestBody CurrentLocationDto currentLocationDto) {
        return ResponseEntity.ok(attendanceService.memberEntrance(payload.getMemberId(), attendanceId, currentLocationDto));
    }

    @PatchMapping("/mypage/attendance/quit/{attendanceId}")
    public ResponseEntity<AttendanceStatus> memberQuit(@RequestHeader JwtPayload payload, @PathVariable Long attendanceId, @Valid @RequestBody CurrentLocationDto currentLocationDto) {
        attendanceService.memberQuit(payload.getMemberId(), attendanceId, currentLocationDto);
        return ResponseEntity.ok().build();
    }
}
