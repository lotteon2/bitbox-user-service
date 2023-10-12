package com.bitbox.user.domain.member.service;

import com.bitbox.user.domain.Attendance;
import com.bitbox.user.service.AttendanceService;
import com.bitbox.user.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootTest
@Transactional
public class AttendanceServiceTest {
    @Autowired
    private AttendanceService attendanceService;

    @BeforeEach
    public void before() {
//        Attendance attendance = Attendance.builder().attendanceDate(LocalDate.now()).
    }
}
