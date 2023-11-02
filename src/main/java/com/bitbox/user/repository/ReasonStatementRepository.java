package com.bitbox.user.repository;

import com.bitbox.user.domain.ReasonStatement;
import com.bitbox.user.service.response.ReasonStatementWithAttendanceAndMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ReasonStatementRepository extends CrudRepository<ReasonStatement, String> {

    ReasonStatement findByReasonStatementId(Long reasonStatementId);

    ReasonStatement findByAttendance_AttendanceId(Long attendnaceId);

    @Query(value = "SELECT new com.bitbox.user.service.response.ReasonStatementWithAttendanceAndMember(r.reasonStatementId, a.attendanceDate, m.memberName, r.reasonTitle, r.reasonContent, r.reasonAttachedFile, r.reasonState, r.read) FROM ReasonStatement r INNER JOIN Attendance a ON r.attendance.attendanceId = a.attendanceId INNER JOIN Member m ON r.member.memberId = m.memberId WHERE m.classId = :classId ORDER BY r.reasonStatementId")
    Page<ReasonStatementWithAttendanceAndMember> findAllByClassIdOrderByReasonStatementIdDesc(Long classId, Pageable paging);

    @Query(value = "SELECT COUNT(r) FROM ReasonStatement  r INNER JOIN Member m ON r.member.memberId = m.memberId WHERE m.classId = :classId")
    Long countByClassId(Long classId);
}
