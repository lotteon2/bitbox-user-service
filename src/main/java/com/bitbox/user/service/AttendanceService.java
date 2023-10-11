package com.bitbox.user.service;

import com.bitbox.user.domain.Attendance;
import com.bitbox.user.dto.CurrentLocationDto;
import com.bitbox.user.exception.InvalidAttendanceException;
import com.bitbox.user.exception.InvalidRangeAttendanceException;
import com.bitbox.user.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final double BIT_CENTER_LAT = 37.4946;
    private final double BIT_CENTER_LNG = 127.0276;

    @Transactional // [TODO] Transactional 관련 설명이 필요할듯..
    public void memberEntrance(Long attendanceId, CurrentLocationDto currentLocationDto) {
        Attendance attendance = getAttendance(attendanceId, currentLocationDto);
        attendance.setEntraceTime(LocalTime.now());
    }

    @Transactional
    public void memberQuit(Long attendanceId, CurrentLocationDto currentLocationDto) {
        Attendance attendance = getAttendance(attendanceId, currentLocationDto);
        attendance.setQuitTime(LocalTime.now());
    }

    private Attendance getAttendance(Long attendanceId, CurrentLocationDto currentLocationDto) {
        Attendance attendance = attendanceRepository.findById(attendanceId).orElseThrow(() -> new InvalidAttendanceException("ERROR120 - 존재하지 않는 출결정보"));
        double lat = currentLocationDto.getLat();
        double lng = currentLocationDto.getLng();

        // 반경 100M 이내
        if (Math.pow(0.001, 2) < (Math.pow(BIT_CENTER_LAT - lat, 2) + Math.pow(BIT_CENTER_LNG - lng, 2))) {
            throw new InvalidRangeAttendanceException("ERROR121 - 교육장과 멀리 떨어져 있습니다");
        }
        return attendance;
    }

}
