package com.bitbox.user.service;

import com.bitbox.user.domain.Attendance;
import com.bitbox.user.dto.CurrentLocationDto;
import com.bitbox.user.exception.InvalidAttendanceException;
import com.bitbox.user.exception.InvalidMemberIdException;
import com.bitbox.user.exception.InvalidRangeAttendanceException;
import com.bitbox.user.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final double bitCenterLat = 37.4946;
    private final double bitCenterLng = 127.0276;

    @Transactional
    public void memberEntrance(Long attendanceId, CurrentLocationDto currentLocationDto) {
        Attendance attendance = attendanceRepository.findById(attendanceId).orElseThrow(() -> new InvalidAttendanceException("ERROR120 - 존재하지 않는 출결정보"));
        double lat = currentLocationDto.getLat();
        double lng = currentLocationDto.getLng();

        // 반경 100M 이내
        if (Math.pow(0.001, 2) >= (Math.pow(bitCenterLat - lat, 2) + Math.pow(bitCenterLng - lng, 2))) {
            attendance.setEntraceTime(LocalTime.now());
        } else {
            throw new InvalidRangeAttendanceException("ERROR121 - 교육장과 멀리 떨어져 있습니다");
        }

    }

    @Transactional
    public void memberQuit(Long attendanceId, CurrentLocationDto currentLocationDto) {
        Attendance attendance = attendanceRepository.findById(attendanceId).orElseThrow(() -> new InvalidAttendanceException("ERROR120 - 존재하지 않는 출결정보"));
        double lat = currentLocationDto.getLat();
        double lng = currentLocationDto.getLng();

        // 반경 100M 이내
        if (Math.pow(0.001, 2) >= (Math.pow(bitCenterLat - lat, 2) + Math.pow(bitCenterLng - lng, 2))) {
            attendance.setQuitTime(LocalTime.now());
        } else {
            throw new InvalidRangeAttendanceException("ERROR121 - 교육장과 멀리 떨어져 있습니다");
        }

    }
}
