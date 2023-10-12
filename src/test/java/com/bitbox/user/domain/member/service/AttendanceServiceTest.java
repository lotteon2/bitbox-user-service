package com.bitbox.user.domain.member.service;

import com.bitbox.user.domain.Attendance;
import com.bitbox.user.domain.Member;
import com.bitbox.user.dto.CurrentLocationDto;
import com.bitbox.user.dto.MemberDto;
import com.bitbox.user.exception.DuplicationEmailException;
import com.bitbox.user.exception.InvalidRangeAttendanceException;
import com.bitbox.user.repository.AttendanceRepository;
import com.bitbox.user.service.AttendanceService;
import com.bitbox.user.service.MemberService;
import io.github.bitbox.bitbox.enums.AttendanceStatus;
import io.github.bitbox.bitbox.enums.AuthorityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

// [TODO] 3개는 한번 고쳐보자!
@SpringBootTest
@Transactional
public class AttendanceServiceTest {
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private MemberService memberService;

    @BeforeEach
    public void before() {
        MemberDto memberDto = MemberDto.builder()
                .memberNickname("김정윤")
                .memberEmail("indl1670@naver.com")
                .memberProfileImg("https://mblogthumb-phinf.pstatic.net/MjAyMDExMDFfMTgy/MDAxNjA0MjI4ODc1NDMw.Ex906Mv9nnPEZGCh4SREknadZvzMO8LyDzGOHMKPdwAg.ZAmE6pU5lhEdeOUsPdxg8-gOuZrq_ipJ5VhqaViubI4g.JPEG.gambasg/%EC%9C%A0%ED%8A%9C%EB%B8%8C_%EA%B8%B0%EB%B3%B8%ED%94%84%EB%A1%9C%ED%95%84_%ED%95%98%EB%8A%98%EC%83%89.jpg?type=w800")
                .memberAuthority(AuthorityType.TRAINEE)
                .build();
        Member memberInfo = memberService.registMemberInfo(memberDto);

        for (int i = 0; i <= 10; i++) {
            addAttendance(i, memberInfo);
        }

    }

    @DisplayName("교육생은 지정된 영역 내에서 입실체크를 할 수 있다. 7시 ~ 9시 사이 출석체크 시 출결처리된다.")
    @Order(1)
    @Test
    public void entrace() {
        CurrentLocationDto location = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("08:00:00").build();
        AttendanceStatus result = attendanceService.memberEntrance(getFirstAttendance().getAttendanceId(), location);


        assertThat(result).isEqualTo(AttendanceStatus.ATTENDANCE);
    }

    @DisplayName("지정된 영역 밖에서 입실체크를 할 때 예외가 발생한다.")
    @Order(2)
    @Test
    public void invalidEntrace() {
        CurrentLocationDto location = CurrentLocationDto.builder().lat(37.5046).lng(127.0376).build();

        assertThatThrownBy(() -> attendanceService.memberEntrance(getFirstAttendance().getAttendanceId(), location))
                .isInstanceOf(InvalidRangeAttendanceException.class)
                .hasMessage("교육장과 멀리 떨어져 있습니다.");
    }

    @DisplayName("교육생은 지정된 영역 내에서 퇴실체크를 할 수 있다.")
    @Order(3)
    @Test
    public void quit() {
        CurrentLocationDto location = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).build();
        attendanceService.memberQuit(getFirstAttendance().getAttendanceId(), location);
    }

    @DisplayName("지정된 영역 밖에서 퇴실체크를 할 때 예외가 발생한다.")
    @Order(4)
    @Test
    public void invalidQuit() {
        CurrentLocationDto location = CurrentLocationDto.builder().lat(37.5046).lng(127.0376).build();

        assertThatThrownBy(() -> attendanceService.memberQuit(getFirstAttendance().getAttendanceId(), location))
                .isInstanceOf(DuplicationEmailException.class)
                .hasMessage("교육장과 멀리 떨어져 있습니다.");
    }

    @DisplayName("내 출결정보 조회 시 전체 출결 정보를 받아온다.")
    @Order(5)
    @Test
    public void getMyAttendance() {
        List<Attendance> attendanceList = attendanceService.getAllMyAttendance("4e5ccba9-5512-46e4-8095-eaffc42a633b");
        assertThat(attendanceList).isNotNull();
    }

    @DisplayName("관리자가 classId를 기준으로 출결 정보를 조회할 때 오늘 포함 이전 7일에 대한 정보들을 받아온다.")
    @Order(6)
    @Test
    public void getMyAttendanceForAdmin() {
        List<Attendance> attendanceList = attendanceService.getAttendanceForAdmin(1);
        LocalDate before = LocalDate.now().minusDays(6);
        LocalDate after = LocalDate.now();

        boolean result = checkAttendanceDate(before, after, attendanceList);
        assertThat(result).isTrue();

    }

    private Attendance getFirstAttendance(){
        List<Attendance> attendanceList = (List<Attendance>) attendanceRepository.findAll();
        return attendanceList.get(0);
    }

    private void addAttendance(int num, Member memberInfo) {
        Attendance attendance = Attendance.builder().attendanceDate(LocalDate.now().minusDays(num)).attendanceState(AttendanceStatus.ABSENT).member(memberInfo).build();
        attendanceRepository.save(attendance);
    }

    private Boolean checkAttendanceDate(LocalDate before, LocalDate after, List<Attendance> attendanceList) {
        for (Attendance attendance: attendanceList) {
            LocalDate current = attendance.getAttendanceDate();

            // 출결 날짜가 오늘 포함 7일 이내에 존재하지 않는 경우 false
            if (!(current.isBefore(after) && current.isAfter(before)) || !(current.equals(before) || current.equals(after))) return false;

        }
        return true;
    }

}
