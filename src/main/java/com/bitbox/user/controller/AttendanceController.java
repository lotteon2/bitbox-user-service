package com.bitbox.user.controller;

import com.bitbox.user.dto.CurrentLocationDto;
import com.bitbox.user.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
