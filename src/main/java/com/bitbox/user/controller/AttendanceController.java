package com.bitbox.user.controller;

import com.bitbox.user.domain.Attendance;
import com.bitbox.user.dto.AttendanceUpdateDto;
import com.bitbox.user.dto.CurrentLocationDto;
import com.bitbox.user.service.AttendanceService;
import io.github.bitbox.bitbox.enums.AttendanceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.PathParam;
import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PatchMapping("/mypage/attendance/entrance/{attendanceId}")
    public ResponseEntity<Void> memberEntrance(@PathVariable Long attendanceId, @RequestBody CurrentLocationDto currentLocationDto) {
        attendanceService.memberEntrance(attendanceId, currentLocationDto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/mypage/attendance/quit/{attendanceId}")
    public ResponseEntity<Void> memberQuit(@PathVariable Long attendanceId, @RequestBody CurrentLocationDto currentLocationDto) {
        attendanceService.memberQuit(attendanceId, currentLocationDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/mypage/attendance")
    public ResponseEntity<List<Attendance>> getAllMyAttendance(@RequestHeader String memberId) {
        return ResponseEntity.ok(attendanceService.getAllMyAttendance(memberId));
    }

    @GetMapping("/admin/attendance/{classId}")
    public ResponseEntity<List<Attendance>> getAllAttendance(@PathVariable Long classId) {
        return ResponseEntity.ok(attendanceService.getAttendanceForAdmin(classId));
    }

    @PatchMapping("/admin/attendance")
    public ResponseEntity<AttendanceStatus> updateAttendancceState(@RequestBody AttendanceUpdateDto attendanceUpdateDto) {
        return ResponseEntity.ok(attendanceService.updateAttendancceState(attendanceUpdateDto));
    }
}
