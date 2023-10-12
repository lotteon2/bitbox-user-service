package com.bitbox.user.controller;

import com.bitbox.user.domain.Attendance;
import com.bitbox.user.dto.AttendanceUpdateDto;
import com.bitbox.user.dto.CurrentLocationDto;
import com.bitbox.user.service.AttendanceService;
import io.github.bitbox.bitbox.enums.AttendanceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/member") // [TODO] 이게 왜 MemberController랑 같아?
@RequiredArgsConstructor
@CrossOrigin("*") // [TODO] 일단 컨트롤러에 붙이기는 했는데 확인 필요.
public class AttendanceController {
    private final AttendanceService attendanceService;

    @GetMapping("/mypage/attendance")
    public ResponseEntity<List<Attendance>> getAllMyAttendance(@RequestHeader String memberId) {
        return ResponseEntity.ok(attendanceService.getAllMyAttendance(memberId));
    }

    @GetMapping("/admin/attendance/{classId}")
    public ResponseEntity<List<Attendance>> getAllAttendance(@PathVariable Long classId) {
        return ResponseEntity.ok(attendanceService.getAttendanceForAdmin(classId));
    }

    @PatchMapping("/admin/attendance")
    public ResponseEntity<AttendanceStatus> updateAttendanceState(@Valid @RequestBody AttendanceUpdateDto attendanceUpdateDto) {
        return ResponseEntity.ok(attendanceService.updateAttendanceState(attendanceUpdateDto));
    }

    @PatchMapping("/mypage/attendance/entrance/{attendanceId}")
    public ResponseEntity<AttendanceStatus> memberEntrance(@PathVariable Long attendanceId, @Valid @RequestBody CurrentLocationDto currentLocationDto) {
        return ResponseEntity.ok(attendanceService.memberEntrance(attendanceId, currentLocationDto));
    }

    @PatchMapping("/mypage/attendance/quit/{attendanceId}")
    public ResponseEntity<Void> memberQuit(@PathVariable Long attendanceId, @Valid @RequestBody CurrentLocationDto currentLocationDto) {
        attendanceService.memberQuit(attendanceId, currentLocationDto);
        return ResponseEntity.ok().build();
    }
}
