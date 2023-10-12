package com.bitbox.user.repository;

import com.bitbox.user.domain.Attendance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends CrudRepository<Attendance, Long> {

    Attendance findByAttendanceId(long attendanceId);

    /**
     * 출결 조회(관리자)
     * classId 기준으로 조회 & 오늘-7 ~ 오늘 날짜까지
     * @param classId
     * @param before
     * @param after
     * @return
     */
    @Query(value = "SELECT a " +
            "FROM Attendance a INNER JOIN Member m ON a.member.memberId = m.memberId " +
            "WHERE m.classId = :classId AND a.attendanceDate BETWEEN :before AND :after ORDER BY a.attendanceDate")
    List<Attendance> findByClassIdForAdmin(long classId, LocalDate before, LocalDate after);

    /**
     * 내 출결 조회
     * @param memberId
     * @return
     */
    @Query(value = "SELECT a " +
            "FROM Attendance  a WHERE a.member.memberId = :memberId ORDER BY a.attendanceDate")
    List<Attendance> findByMemberId(String memberId);



}
