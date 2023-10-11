package com.bitbox.user.domain;

import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "attendance")
@DynamicInsert
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long attendanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "entrace_time")
    private LocalTime entraceTime;

    @Column(name = "quit_time")
    private LocalTime quitTime;

    @Column(name = "attendance_state", nullable = false)
    private String attendanceState;

    @Column(name = "attendance_modify_reason")
    private String attendanceModifyReason;
}
