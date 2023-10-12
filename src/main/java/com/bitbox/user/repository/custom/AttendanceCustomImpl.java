package com.bitbox.user.repository.custom;

import com.bitbox.user.domain.Attendance;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.bitbox.user.domain.QAttendance.attendance;

@RequiredArgsConstructor
public class AttendanceCustomImpl implements AttendanceCustom{
    private final JPAQueryFactory query;
    @Override
    public Optional<Attendance> findByIdFetch(Long id) {
        return Optional.ofNullable(
                query.selectFrom(attendance)
                        //.join(attendance.member).fetchJoin()
                        .where(attendance.attendanceId.eq(id))
                        .fetchOne());
    }
}
