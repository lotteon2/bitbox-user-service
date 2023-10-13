package com.bitbox.user.domain.member.service;

import com.bitbox.user.domain.Attendance;
import com.bitbox.user.domain.Member;
import com.bitbox.user.dto.AttendanceUpdateDto;
import com.bitbox.user.dto.CurrentLocationDto;
import com.bitbox.user.dto.MemberDto;
import com.bitbox.user.exception.InvalidAttendanceRequestException;
import com.bitbox.user.exception.InvalidRangeAttendanceException;
import com.bitbox.user.repository.AttendanceRepository;
import com.bitbox.user.service.AttendanceService;
import com.bitbox.user.service.MemberService;
import com.bitbox.user.service.response.AvgAttendanceInfo;
import com.bitbox.user.service.response.MemberInfoWithAttendance;
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

@SpringBootTest
@Transactional
public class AttendanceServiceTest {
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private MemberService memberService;

    private String memberId;

    @BeforeEach
    public void before() {
        MemberDto memberDto = MemberDto.builder()
                .memberNickname("김정윤")
                .memberEmail("indl1670@naver.com")
                .memberProfileImg("https://mblogthumb-phinf.pstatic.net/MjAyMDExMDFfMTgy/MDAxNjA0MjI4ODc1NDMw.Ex906Mv9nnPEZGCh4SREknadZvzMO8LyDzGOHMKPdwAg.ZAmE6pU5lhEdeOUsPdxg8-gOuZrq_ipJ5VhqaViubI4g.JPEG.gambasg/%EC%9C%A0%ED%8A%9C%EB%B8%8C_%EA%B8%B0%EB%B3%B8%ED%94%84%EB%A1%9C%ED%95%84_%ED%95%98%EB%8A%98%EC%83%89.jpg?type=w800")
                .memberAuthority(AuthorityType.TRAINEE)
                .classId(1L)
                .build();
        Member memberInfo = memberService.registMemberInfo(memberDto);
        memberId = memberInfo.getMemberId();

        for (int i = 0; i <= 10; i++) {
            addAttendance(i, memberInfo);
        }

    }

    @DisplayName("교육생은 지정된 영역 내에서 입실체크를 할 수 있다. 7시 ~ 9시 사이 입실체크 시 출결처리된다.")
    @Order(1)
    @Test
    public void entraceAttendance() {
        CurrentLocationDto location = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("08:00:00").build();
        AttendanceStatus result = attendanceService.memberEntrance(memberId, getFirstAttendance().getAttendanceId(), location);


        assertThat(result).isEqualTo(AttendanceStatus.ATTENDANCE);
    }

    @DisplayName("교육생은 지정된 영역 내에서 입실체크를 할 수 있다. 9시 ~ 14시 사이 입실체크 시 지각처리된다.")
    @Order(2)
    @Test
    public void entraceTardy() {
        CurrentLocationDto location = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("11:00:00").build();
        AttendanceStatus result = attendanceService.memberEntrance(memberId, getFirstAttendance().getAttendanceId(), location);

        assertThat(result).isEqualTo(AttendanceStatus.TARDY);
    }

    @DisplayName("교육생은 지정된 영역 내에서 입실체크를 할 수 있다. 7시 ~ 14시를 제외한 시간에 입실체크 시 예외를 호출하며 이전 출결 상태 그대로 반영된다.")
    @Order(3)
    @Test
    public void invalidEntrace() {
        CurrentLocationDto location = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("06:00:00").build();
        assertThatThrownBy(() -> attendanceService.memberEntrance(memberId, getFirstAttendance().getAttendanceId(), location))
                .isInstanceOf(InvalidAttendanceRequestException.class)
                .hasMessage("유효하지 않은 출결 요청입니다.");

        Attendance attendance = attendanceRepository.findByAttendanceId(getFirstAttendance().getAttendanceId());
        assertThat(attendance.getAttendanceState()).isEqualTo(AttendanceStatus.ABSENT);
    }

    @DisplayName("교육생은 지정된 영역 내에서 입실체크를 할 수 있다. 입실은 유효한 첫 요청만 저장한다.")
    @Order(4)
    @Test
    public void checkFirstEntrace() {
        CurrentLocationDto location = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("08:00:00").build();
        attendanceService.memberEntrance(memberId, getFirstAttendance().getAttendanceId(), location);

        CurrentLocationDto location2 = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("11:00:00").build();
        AttendanceStatus result = attendanceService.memberEntrance(memberId, getFirstAttendance().getAttendanceId(), location2);

        assertThat(result).isEqualTo(AttendanceStatus.ATTENDANCE);
    }

    @DisplayName("교육생이 지정된 영역 밖에서 입실체크할 경우 예외가 발생한다.")
    @Order(5)
    @Test
    public void outofRangeEntrace() {
        CurrentLocationDto location = CurrentLocationDto.builder().lat(37.5046).lng(127.0376).current("08:00:00").build();

        assertThatThrownBy(() -> attendanceService.memberEntrance(memberId, getFirstAttendance().getAttendanceId(), location))
                .isInstanceOf(InvalidRangeAttendanceException.class)
                .hasMessage("교육장과 멀리 떨어져 있습니다.");
    }

    @DisplayName("타인의 입실을 체크할 경우 예외가 발생한다.")
    @Order(6)
    @Test
    public void otherMemberEntrace() {
        MemberDto memberDto = MemberDto.builder()
                .memberNickname("테스트")
                .memberEmail("test@naver.com")
                .memberProfileImg("https://mblogthumb-phinf.pstatic.net/MjAyMDExMDFfMTgy/MDAxNjA0MjI4ODc1NDMw.Ex906Mv9nnPEZGCh4SREknadZvzMO8LyDzGOHMKPdwAg.ZAmE6pU5lhEdeOUsPdxg8-gOuZrq_ipJ5VhqaViubI4g.JPEG.gambasg/%EC%9C%A0%ED%8A%9C%EB%B8%8C_%EA%B8%B0%EB%B3%B8%ED%94%84%EB%A1%9C%ED%95%84_%ED%95%98%EB%8A%98%EC%83%89.jpg?type=w800")
                .memberAuthority(AuthorityType.GENERAL)
                .build();
        Member memberInfo = memberService.registMemberInfo(memberDto);
        String testMemberId = memberInfo.getMemberId();

        CurrentLocationDto location = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("08:00:00").build();
        assertThatThrownBy(() -> attendanceService.memberEntrance(testMemberId, getFirstAttendance().getAttendanceId(), location))
                .isInstanceOf(InvalidAttendanceRequestException.class)
                .hasMessage("유효하지 않은 출결 요청입니다.");
    }

    @DisplayName("교육생은 지정된 영역 내에서 퇴실체크를 할 수 있다. 정상 입실된 상태에서 18시 ~ 22시 30분 사이 퇴실체크할 경우 출석이 인정된다.")
    @Order(7)
    @Test
    public void quitAttendance() {
        CurrentLocationDto entrace = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("08:00:00").build();
        attendanceService.memberEntrance(memberId, getFirstAttendance().getAttendanceId(), entrace);

        CurrentLocationDto quit = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("22:00:00").build();
        AttendanceStatus result = attendanceService.memberQuit(memberId, getFirstAttendance().getAttendanceId(), quit);

        assertThat(result).isEqualTo(AttendanceStatus.ATTENDANCE);
    }

    @DisplayName("교육생은 지정된 영역 내에서 퇴실체크를 할 수 있다. 정상 입실된 상태에서 14시 ~ 18시에 퇴실체크할 경우 조퇴처리된다.")
    @Order(8)
    @Test
    public void quitGoOut() {
        CurrentLocationDto entrace = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("08:00:00").build();
        attendanceService.memberEntrance(memberId, getFirstAttendance().getAttendanceId(), entrace);

        CurrentLocationDto quit = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("17:00:00").build();
        AttendanceStatus result = attendanceService.memberQuit(memberId, getFirstAttendance().getAttendanceId(), quit);

        assertThat(result).isEqualTo(AttendanceStatus.LEAVE_EARLY);
    }

    @DisplayName("교육생은 지정된 영역 내에서 퇴실체크를 할 수 있다. 지각으로 입실체크한 상태에서 14시 ~ 22시 30분 사이 퇴실체크할 경우 그대로 지각 상태로 남아있는다.")
    @Order(9)
    @Test
    public void quitTardy() {
        CurrentLocationDto entrace = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("11:00:00").build();
        attendanceService.memberEntrance(memberId, getFirstAttendance().getAttendanceId(), entrace);

        CurrentLocationDto quit = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("15:00:00").build();
        AttendanceStatus result = attendanceService.memberQuit(memberId, getFirstAttendance().getAttendanceId(), quit);

        assertThat(result).isEqualTo(AttendanceStatus.TARDY); // 정상 입실 상태에서 퇴실 정보가 저장되지 않는 경우 배치를 통해 조퇴 처리
    }

    @DisplayName("교육생은 지정된 영역 내에서 퇴실체크를 할 수 있다. 14시 ~ 22시 30분을 제외한 시간에 퇴실체크 시 예외를 호출하며 이전 출결 상태 그대로 반영된다.")
    @Order(10)
    @Test
    public void invalidQuit() {
        // 출석
        CurrentLocationDto entrace = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("08:00:00").build();
        attendanceService.memberEntrance(memberId, getFirstAttendance().getAttendanceId(), entrace);

        CurrentLocationDto quit = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("13:00:00").build();
        assertThatThrownBy(() -> attendanceService.memberQuit(memberId, getFirstAttendance().getAttendanceId(), quit))
                .isInstanceOf(InvalidAttendanceRequestException.class)
                .hasMessage("유효하지 않은 출결 요청입니다.");

        Attendance attendance = attendanceRepository.findByAttendanceId(getFirstAttendance().getAttendanceId());
        assertThat(attendance.getAttendanceState()).isEqualTo(AttendanceStatus.ATTENDANCE); // 정상 입실 상태에서 퇴실 정보가 저장되지 않는 경우 배치를 통해 조퇴 처리

        // 지각
        CurrentLocationDto entrace2 = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("11:00:00").build();
        attendanceService.memberEntrance(memberId, getFirstAttendance().getAttendanceId() + 1, entrace2);

        CurrentLocationDto quit2 = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("13:00:00").build();
        assertThatThrownBy(() -> attendanceService.memberQuit(memberId, getFirstAttendance().getAttendanceId() + 1, quit2))
                .isInstanceOf(InvalidAttendanceRequestException.class)
                .hasMessage("유효하지 않은 출결 요청입니다.");

        Attendance attendance2 = attendanceRepository.findByAttendanceId(getFirstAttendance().getAttendanceId() + 1);
        assertThat(attendance2.getAttendanceState()).isEqualTo(AttendanceStatus.TARDY);

        // 입실 기록 없음
        CurrentLocationDto entrace3 = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("15:00:00").build();
        assertThatThrownBy(() -> attendanceService.memberEntrance(memberId, getFirstAttendance().getAttendanceId() + 2, entrace3))
                .isInstanceOf(InvalidAttendanceRequestException.class)
                .hasMessage("유효하지 않은 출결 요청입니다.");

        CurrentLocationDto quit3 = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("20:00:00").build();
        AttendanceStatus result = attendanceService.memberQuit(memberId, getFirstAttendance().getAttendanceId() + 2, quit3);
        assertThat(result).isEqualTo(AttendanceStatus.ABSENT);
    }

    @DisplayName("교육생이 지정된 영역 밖에서 퇴실체크할 경우 예외가 발생한다.")
    @Order(11)
    @Test
    public void outofRangeQuit() {
        CurrentLocationDto location = CurrentLocationDto.builder().lat(37.5046).lng(127.0376).current("22:00:00").build();

        assertThatThrownBy(() -> attendanceService.memberQuit(memberId, getFirstAttendance().getAttendanceId(), location))
                .isInstanceOf(InvalidRangeAttendanceException.class)
                .hasMessage("교육장과 멀리 떨어져 있습니다.");
    }

    @DisplayName("타인의 퇴실을 체크할 경우 예외가 발생한다.")
    @Order(12)
    @Test
    public void otherMemberQuit() {
        MemberDto memberDto = MemberDto.builder()
                .memberNickname("테스트")
                .memberEmail("test@naver.com")
                .memberProfileImg("https://mblogthumb-phinf.pstatic.net/MjAyMDExMDFfMTgy/MDAxNjA0MjI4ODc1NDMw.Ex906Mv9nnPEZGCh4SREknadZvzMO8LyDzGOHMKPdwAg.ZAmE6pU5lhEdeOUsPdxg8-gOuZrq_ipJ5VhqaViubI4g.JPEG.gambasg/%EC%9C%A0%ED%8A%9C%EB%B8%8C_%EA%B8%B0%EB%B3%B8%ED%94%84%EB%A1%9C%ED%95%84_%ED%95%98%EB%8A%98%EC%83%89.jpg?type=w800")
                .memberAuthority(AuthorityType.GENERAL)
                .build();
        Member memberInfo = memberService.registMemberInfo(memberDto);
        String testMemberId = memberInfo.getMemberId();

        CurrentLocationDto location = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("22:00:00").build();
        assertThatThrownBy(() -> attendanceService.memberQuit(testMemberId, getFirstAttendance().getAttendanceId(), location))
                .isInstanceOf(InvalidAttendanceRequestException.class)
                .hasMessage("유효하지 않은 출결 요청입니다.");
    }


    @DisplayName("내 출결정보 조회 시 전체 출결 정보를 받아온다.")
    @Order(13)
    @Test
    public void getMyAttendance() {
        List<Attendance> attendanceList = attendanceService.getAllMyAttendance(memberId);
        assertThat(attendanceList).isNotNull();
    }

    @DisplayName("관리자가 classId를 기준으로 출결 정보를 조회할 수 있다.")
    @Order(14)
    @Test
    public void getAllAttendanceForAdmin() {
        CurrentLocationDto entrace = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("08:00:00").build();
        attendanceService.memberEntrance(memberId, getFirstAttendance().getAttendanceId(), entrace);
        CurrentLocationDto quit = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("22:00:00").build();
        attendanceService.memberQuit(memberId, getFirstAttendance().getAttendanceId(), quit);

        List<MemberInfoWithAttendance> attendanceList = attendanceService.getAttendanceForAdmin(1, null, null);
        assertThat(attendanceList.size()).isEqualTo(11);
    }
    @DisplayName("관리자가 classId를 기준으로 출결 정보를 조회할 수 있다. 특정 날짜로 필터링할 수 있다.")
    @Order(15)
    @Test
    public void getAllAttendanceForAdminWithSelectedDate() {
        CurrentLocationDto entrace = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("08:00:00").build();
        attendanceService.memberEntrance(memberId, getFirstAttendance().getAttendanceId(), entrace);
        CurrentLocationDto quit = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("22:00:00").build();
        attendanceService.memberQuit(memberId, getFirstAttendance().getAttendanceId(), quit);

        List<MemberInfoWithAttendance> attendanceList = attendanceService.getAttendanceForAdmin(1, LocalDate.now(), null);
        assertThat(attendanceList.size()).isEqualTo(1);
    }

    @DisplayName("관리자가 classId를 기준으로 출결 정보를 조회할 수 있다. 특정 교육생 이름으로 필터링할 수 있다.")
    @Order(16)
    @Test
    public void getAllAttendanceForAdminWithStudentName() {
        CurrentLocationDto entrace = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("08:00:00").build();
        attendanceService.memberEntrance(memberId, getFirstAttendance().getAttendanceId(), entrace);
        CurrentLocationDto quit = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("22:00:00").build();
        attendanceService.memberQuit(memberId, getFirstAttendance().getAttendanceId(), quit);

        List<MemberInfoWithAttendance> attendanceList = attendanceService.getAttendanceForAdmin(1, null, "김");
        assertThat(attendanceList.size()).isEqualTo(11);
    }

    @DisplayName("관리자가 classId를 기준으로 출결 정보를 조회할 수 있다. 특정 날짜와 특정 교육생으로 필터링할 수 있다.")
    @Order(17)
    @Test
    public void getAllAttendanceForAdminWithSelectedDateAndStudentName() {
        CurrentLocationDto entrace = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("08:00:00").build();
        attendanceService.memberEntrance(memberId, getFirstAttendance().getAttendanceId(), entrace);
        CurrentLocationDto quit = CurrentLocationDto.builder().lat(37.4946).lng(127.0276).current("22:00:00").build();
        attendanceService.memberQuit(memberId, getFirstAttendance().getAttendanceId(), quit);

        List<MemberInfoWithAttendance> attendanceList = attendanceService.getAttendanceForAdmin(1, LocalDate.now(), "김");
        assertThat(attendanceList.size()).isEqualTo(1);
    }
    @DisplayName("관리자가 classId를 기준으로 반별 출석 인원 현황을 조회할 수 있다.")
    @Order(18)
    @Test
    public void getAllAttendanceForAdminDashboard() {
        List<AvgAttendanceInfo> attendanceList = attendanceService.getAttendanceForDashboard(1);
        boolean result = checkAttendanceDate(LocalDate.now().minusDays(6), LocalDate.now(), attendanceList);
        assertThat(result).isTrue();
    }

    @DisplayName("관리자는 학생들의 출결 상태를 수정할 수 있다.")
    @Order(19)
    @Test
    public void updateAttendanceState() {
        AttendanceUpdateDto update = AttendanceUpdateDto.builder().attendanceId(getFirstAttendance().getAttendanceId()).attendanceState(AttendanceStatus.ATTENDANCE).attendanceModifyReason("TEST").build();
        AttendanceStatus result = attendanceService.updateAttendanceState(update);

        assertThat(result).isEqualTo(AttendanceStatus.ATTENDANCE);
    }

    private Attendance getFirstAttendance(){
        List<Attendance> attendanceList = (List<Attendance>) attendanceRepository.findAll();
        return attendanceList.get(0);
    }

    private void addAttendance(int num, Member memberInfo) {
        Attendance attendance = Attendance.builder().attendanceDate(LocalDate.now().minusDays(num)).attendanceState(AttendanceStatus.ABSENT).member(memberInfo).build();
        attendanceRepository.save(attendance);
    }

    private Boolean checkAttendanceDate(LocalDate before, LocalDate after, List<AvgAttendanceInfo> attendanceList) {
        for (AvgAttendanceInfo attendance: attendanceList) {
            LocalDate current = attendance.getName();

            // 출결 날짜가 오늘 포함 7일 이내에 존재하지 않는 경우 false
            if (!(current.isBefore(after) && current.isAfter(before))) {
                if (current.isBefore(after) && !current.equals(before)) {
                    return false;
                } else if (current.isAfter(before) && !current.equals(after)) {
                    return false;
                }
            }

        }
        return true;
    }

}
