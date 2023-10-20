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

    List<Attendance> findAllByMember_MemberId(String memberId);

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


    @Query(value = "SELECT a FROM Attendance a " +
            "WHERE a.member.memberId = :memberId AND a.attendanceDate = :attendanceDate")
    Attendance findByMemberIdAndAttendanceDate(String memberId, LocalDate attendanceDate);
}
