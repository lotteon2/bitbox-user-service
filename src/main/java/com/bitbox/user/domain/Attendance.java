package com.bitbox.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.bitbox.bitbox.enums.AttendanceStatus;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name="attendance")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long attendanceId;

    @JsonIgnore
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
    private AttendanceStatus attendanceState;

    @Column(name = "attendance_modify_reason")
    private String attendanceModifyReason;
}
