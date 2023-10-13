package com.bitbox.user.repository.custom;

import com.bitbox.user.domain.Attendance;
import com.bitbox.user.domain.QMember;
import com.bitbox.user.domain.QReasonStatement;
import com.bitbox.user.service.response.MemberInfoWithAttendance;
import com.bitbox.user.service.response.QMemberInfoWithAttendance;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
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
                        //.join(attendance.member).fetchJoin()
                        .where(attendance.attendanceId.eq(id))
                        .fetchOne());
    }

    @Override
    public List<MemberInfoWithAttendance> findByClassIdForAdmin(Long classId, LocalDate current, String memberNickname) {
        List<MemberInfoWithAttendance> fetch = query.select(new QMemberInfoWithAttendance(member.memberId, member.memberProfileImg, member.memberNickname, attendance.attendanceId, attendance.attendanceDate, attendance.attendanceDate.stringValue().concat(" ").concat(attendance.entraceTime.stringValue()).as("entrace") , attendance.attendanceDate.stringValue().concat(" ").concat(attendance.quitTime.stringValue()).as("quit"), attendance.attendanceState, attendance.attendanceModifyReason, reasonStatement.reasonTitle))
                .from(attendance)
                .innerJoin(attendance.member, member)
                .leftJoin(reasonStatement)
                .on(attendance.attendanceId.eq(reasonStatement.attendance.attendanceId))
                .where(member.classId.eq(classId), memberNicknameEq(memberNickname),
                        attendanceEq(current))
                .orderBy(attendance.attendanceDate.desc(), member.memberNickname.asc())
                .fetch();
        return fetch;
    }

    private BooleanExpression memberNicknameEq(String memberNickname) {
        if(memberNickname == null || memberNickname.isBlank()) {
            return null;
        }
        return member.memberNickname.contains(memberNickname);
    }
    private BooleanExpression attendanceEq(LocalDate current) {
        return current != null ? attendance.attendanceDate.eq(current) : null;
    }
}
