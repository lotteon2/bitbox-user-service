package com.bitbox.user.util;

import com.bitbox.user.dto.CurrentLocationDto;
import com.bitbox.user.exception.InvalidRangeAttendanceException;

import java.time.LocalTime;

public class AttendanceUtil {
    public static final double BIT_CENTER_LAT = 37.4946;
    public static final double BIT_CENTER_LNG = 127.0276;

    public static final LocalTime BEFORE_ATTENDANCE_TIME_ENTRACE = LocalTime.of(7, 0, 0);
    public static final LocalTime ATTENDANCE_TIME_ENTRACE = LocalTime.of(9, 0,0);
    public static final LocalTime ATTENDANCE_TIME_VALID = LocalTime.of(14, 0, 0);
    public static final LocalTime ATTENDANCE_TIME_QUIT = LocalTime.of(18, 0, 0);
    public static final LocalTime AFTER_ATTENDANCE_TIME_QUIT = LocalTime.of(22, 30, 0);

    public static void checkLocation(double lat, double lng) {
        if (Math.pow(0.001, 2) <= (Math.pow(BIT_CENTER_LAT - lat, 2) + Math.pow(BIT_CENTER_LNG - lng, 2))) {
            throw new InvalidRangeAttendanceException("교육장과 멀리 떨어져 있습니다.");
        }
    }

    public static LocalTime getCurrent(CurrentLocationDto currentLocationDto){
        LocalTime current;

        if (currentLocationDto.getCurrent() == null) current = LocalTime.now();
        else current = LocalTime.of(Integer.parseInt(currentLocationDto.getCurrent().split(":")[0]), Integer.parseInt(currentLocationDto.getCurrent().split(":")[1]), Integer.parseInt(currentLocationDto.getCurrent().split(":")[2]));


        return current;
    }
}
