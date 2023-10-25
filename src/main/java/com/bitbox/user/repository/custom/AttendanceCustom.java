package com.bitbox.user.repository.custom;

import com.bitbox.user.domain.Attendance;
import com.bitbox.user.service.response.MemberInfoWithAttendance;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceCustom {
    Optional<Attendance> findByIdFetch(Long id);

    List<MemberInfoWithAttendance> findByClassIdForAdmin(Long classId, String current, String memberName);
}