package com.bitbox.user.repository.custom;

import com.bitbox.user.domain.Attendance;
import com.bitbox.user.service.response.MemberInfoWithAttendance;
import com.bitbox.user.service.response.QMemberInfoWithAttendance;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.bitbox.user.domain.QAttendance.attendance;
import static com.bitbox.user.domain.QMember.*;
import static com.bitbox.user.domain.QReasonStatement.*;

@RequiredArgsConstructor
public class AttendanceCustomImpl implements AttendanceCustom{
    private final JPAQueryFactory query;
    @Override
    public Optional<Attendance> findByIdFetch(Long id) {
        return Optional.ofNullable(
                query.selectFrom(attendance)
                        .where(attendance.attendanceId.eq(id))
                        .fetchOne());
    }

    @Override
    public List<MemberInfoWithAttendance> findByClassIdForAdmin(Long classId, LocalDate current, String memberName) {
        List<MemberInfoWithAttendance> fetch = query.select(new QMemberInfoWithAttendance(member.memberId, member.memberProfileImg, member.memberName, attendance.attendanceId, attendance.attendanceDate, attendance.entraceTime.stringValue(), attendance.quitTime.stringValue(), attendance.attendanceState, attendance.attendanceModifyReason, reasonStatement.reasonTitle))
                .from(attendance)
                .innerJoin(attendance.member, member)
                .leftJoin(reasonStatement)
                .on(attendance.attendanceId.eq(reasonStatement.attendance.attendanceId))
                .where(member.classId.eq(classId), memberNameEq(memberName),
                        attendanceEq(current))
                .orderBy(attendance.attendanceDate.desc(), member.memberNickname.asc())
                .fetch();
        return fetch;
    }

    private BooleanExpression memberNameEq(String memberName) {
        if(memberName == null || memberName.isBlank()) {
            return null;
        }
        return member.memberNickname.contains(memberName);
    }
    private BooleanExpression attendanceEq(LocalDate current) {
        return current != null ? attendance.attendanceDate.eq(current) : null;
    }
}
