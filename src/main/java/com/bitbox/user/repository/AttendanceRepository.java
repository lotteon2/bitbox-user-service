package com.bitbox.user.repository;

import com.bitbox.user.domain.Attendance;
import com.bitbox.user.repository.custom.AttendanceCustom;
import com.bitbox.user.service.response.AvgAttendanceInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends CrudRepository<Attendance, Long> , AttendanceCustom {

    Attendance findByAttendanceId(long attendanceId);

    /**
     * 대시보드용 출결 조회
     * @param classId
     * @param before
     * @param after
     * @return
     */
    @Query(value = "SELECT new com.bitbox.user.service.response.AvgAttendanceInfo(a.attendanceDate, COUNT(a.attendanceId) ) " +
            "FROM Attendance a INNER JOIN Member m ON a.member.memberId = m.memberId " +
            "WHERE m.classId = :classId AND a.attendanceState = 'ATTENDANCE' AND a.attendanceDate BETWEEN :before AND :after " +
            "GROUP BY a.attendanceDate")
    List<AvgAttendanceInfo> findByClassIdForAdminDashBoard(long classId, LocalDate before, LocalDate after);

    /**
     * TODO: QueryDsl
     * 출결 조회(관리자)
     * @param classId
     * @return
     */
//    @Query(value = "SELECT a " +
//            "FROM Attendance a INNER JOIN Member m ON a.member.memberId = m.memberId " +
//            "WHERE m.classId = :classId AND a.attendanceDate BETWEEN :before AND :after ORDER BY a.attendanceDate")
//    @Query(value = "SELECT new com.bitbox.user.service.response.MemberInfoWithAttendance( " +
//            "m.memberId, m.memberProfileImg, m.memberNickname, a.attendanceId, a.attendanceDate, CONCAT(a.attendanceDate, ' ', a.entraceTime) , CONCAT(a.attendanceDate, ' ', a.quitTime), a.attendanceState, a.attendanceModifyReason, r.reasonTitle) " +
//            "FROM Attendance a INNER JOIN Member m ON a.member.memberId = m.memberId " +
//            "LEFT JOIN ReasonStatement r ON a.attendanceId = r.attendance.attendanceId WHERE m.classId = :classId ORDER BY a.attendanceDate DESC, m.memberNickname")
//    List<MemberInfoWithAttendance> findByClassIdForAdmin(long classId);

    /**
     * 내 출결 조회
     * @param memberId
     * @return
     */
    @Query(value = "SELECT a " +
            "FROM Attendance  a WHERE a.member.memberId = :memberId ORDER BY a.attendanceDate")
    List<Attendance> findByMemberId(String memberId);



}
