package com.bitbox.user.repository;

import com.bitbox.user.domain.Attendance;
import com.bitbox.user.repository.custom.AttendanceCustom;
import com.bitbox.user.service.response.AvgAttendanceInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends CrudRepository<Attendance, Long> , AttendanceCustom {

    Attendance findByAttendanceId(long attendanceId);

    List<Attendance> findAllByMember_MemberId(String memberId);

    /**
     * 대시보드용 출결 조회
     * @param classId
     * @param before
     * @param after
     * @return
     */
    @Query(value = "SELECT new com.bitbox.user.service.response.AvgAttendanceInfo(a.attendanceDate, SUM(CASE WHEN m.classId = :classId AND a.attendanceState = 'ATTENDANCE' THEN 1 ELSE 0 END)) " +
            "FROM Attendance a INNER JOIN Member m ON a.member.memberId = m.memberId " +
            "WHERE a.attendanceDate BETWEEN :before AND :after " +
            "GROUP BY a.attendanceDate")
    List<AvgAttendanceInfo> findByClassIdForAdminDashBoard(@Param("classId") long classId, @Param("before") LocalDate before, @Param("after") LocalDate after);


    @Query(value = "SELECT a FROM Attendance a " +
            "WHERE a.member.memberId = :memberId AND a.attendanceDate = :attendanceDate")
    Attendance findByMemberIdAndAttendanceDate(@Param("memberId") String memberId, @Param("attendanceDate") LocalDate attendanceDate);
}
