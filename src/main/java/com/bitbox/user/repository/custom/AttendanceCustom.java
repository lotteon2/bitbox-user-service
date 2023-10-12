package com.bitbox.user.repository.custom;

import com.bitbox.user.domain.Attendance;

import java.util.Optional;

public interface AttendanceCustom {
    Optional<Attendance> findByIdFetch(Long id);
}