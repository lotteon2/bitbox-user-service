package com.bixbox.user.domain.member.service;

import com.bixbox.user.domain.Member;
import com.bixbox.user.dto.MemberDto;
import com.bixbox.user.dto.MemberUpdateDto;
import com.bixbox.user.exception.DuplicationEmailException;
import com.bixbox.user.exception.InvalidMemberIdException;
import com.bixbox.user.repository.MemberInfoRepository;
import com.bixbox.user.service.MemberService;
import com.bixbox.user.service.response.MemberInfoResponse;
import com.netflix.discovery.converters.Auto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class MemberServiceTest {
    @Autowired
    private MemberService memberService;


    @DisplayName("회원정보를 받아 회원DB에 등록한다.")
    @Order(1)
    @Test
    public void createMember() {
        MemberDto memberDto = MemberDto.builder()
                .memberNickname("김정윤")
                .memberEmail("indl1670@naver.com")
                .memberProfileImg("https://mblogthumb-phinf.pstatic.net/MjAyMDExMDFfMTgy/MDAxNjA0MjI4ODc1NDMw.Ex906Mv9nnPEZGCh4SREknadZvzMO8LyDzGOHMKPdwAg.ZAmE6pU5lhEdeOUsPdxg8-gOuZrq_ipJ5VhqaViubI4g.JPEG.gambasg/%EC%9C%A0%ED%8A%9C%EB%B8%8C_%EA%B8%B0%EB%B3%B8%ED%94%84%EB%A1%9C%ED%95%84_%ED%95%98%EB%8A%98%EC%83%89.jpg?type=w800")
                .memberAuthority("TRAINEE")
                .build();

        Member memberInfo = memberService.registMemberInfo(memberDto);
        assertThat(memberInfo).isNotNull();
    }

    @DisplayName("회원 등록 시 이미 등록된 이메일일 경우 예외가 발생한다.")
    @Order(2)
    @Test
    public void createMemberDuplicationEmail() throws DuplicationEmailException {
        MemberDto memberDto = MemberDto.builder()
                .memberNickname("김정윤")
                .memberEmail("indl1670@naver.com")
                .memberProfileImg("https://mblogthumb-phinf.pstatic.net/MjAyMDExMDFfMTgy/MDAxNjA0MjI4ODc1NDMw.Ex906Mv9nnPEZGCh4SREknadZvzMO8LyDzGOHMKPdwAg.ZAmE6pU5lhEdeOUsPdxg8-gOuZrq_ipJ5VhqaViubI4g.JPEG.gambasg/%EC%9C%A0%ED%8A%9C%EB%B8%8C_%EA%B8%B0%EB%B3%B8%ED%94%84%EB%A1%9C%ED%95%84_%ED%95%98%EB%8A%98%EC%83%89.jpg?type=w800")
                .memberAuthority("GENERAL")
                .build();

        memberService.registMemberInfo(memberDto);

        assertThatThrownBy(() -> memberService.registMemberInfo(memberDto))
                .isInstanceOf(DuplicationEmailException.class)
                .hasMessage("ERROR100 - 중복 이메일 에러");

    }

    @DisplayName("일반 회원은 본인의 정보를 조회할 수 있다.")
    @Order(3)
    @Test
    void getMyInfo() {
        MemberDto memberDto = MemberDto.builder()
                .memberNickname("김정윤")
                .memberEmail("indl1670@naver.com")
                .memberProfileImg("https://mblogthumb-phinf.pstatic.net/MjAyMDExMDFfMTgy/MDAxNjA0MjI4ODc1NDMw.Ex906Mv9nnPEZGCh4SREknadZvzMO8LyDzGOHMKPdwAg.ZAmE6pU5lhEdeOUsPdxg8-gOuZrq_ipJ5VhqaViubI4g.JPEG.gambasg/%EC%9C%A0%ED%8A%9C%EB%B8%8C_%EA%B8%B0%EB%B3%B8%ED%94%84%EB%A1%9C%ED%95%84_%ED%95%98%EB%8A%98%EC%83%89.jpg?type=w800")
                .memberAuthority("GENERAL")
                .build();

        Member member = memberService.registMemberInfo(memberDto);

        MemberInfoResponse memberInfo = memberService.getMyInfo(member.getMemberId());

        assertThat(memberInfo).isNotNull();
    }

    @DisplayName("유효하지 않은 회원 아이디로 조회 시 에러가 발생한다.")
    @Order(4)
    @Test
    void getMyInfoInvalidMemberId() {
        assertThatThrownBy(() -> memberService.getMyInfo("a"))
                .isInstanceOf(InvalidMemberIdException.class)
                .hasMessage("ERROR101 - 존재하지 않는 회원정보");
    }

    @DisplayName("사용자 정보를 입력받아 해당하는 값만 수정한다.")
    @Order(5)
    @Test
    void updateTest() {
        MemberDto memberDto = MemberDto.builder()
            .memberNickname("김정윤")
            .memberEmail("indl1670@naver.com")
            .memberProfileImg("https://mblogthumb-phinf.pstatic.net/MjAyMDExMDFfMTgy/MDAxNjA0MjI4ODc1NDMw.Ex906Mv9nnPEZGCh4SREknadZvzMO8LyDzGOHMKPdwAg.ZAmE6pU5lhEdeOUsPdxg8-gOuZrq_ipJ5VhqaViubI4g.JPEG.gambasg/%EC%9C%A0%ED%8A%9C%EB%B8%8C_%EA%B8%B0%EB%B3%B8%ED%94%84%EB%A1%9C%ED%95%84_%ED%95%98%EB%8A%98%EC%83%89.jpg?type=w800")
            .memberAuthority("GENERAL")
            .build();

        Member member = memberService.registMemberInfo(memberDto);

        MemberUpdateDto updateInfo = MemberUpdateDto.builder().memberNickname("수정완료").memberProfileImg("test.png").memberAuthority("GENERAL").build();
        Member infoResult = memberService.updateMemberInfo(member.getMemberId(), updateInfo);

        assertThat(infoResult).isNotNull();
    }

    @DisplayName("회원 탈퇴를 요청하면 해당 계정의 삭제 여부는 true가 된다.")
    @Order(6)
    @Test
    void deleteTest() {
        MemberDto memberDto = MemberDto.builder()
                .memberNickname("김정윤")
                .memberEmail("indl1670@naver.com")
                .memberProfileImg("https://mblogthumb-phinf.pstatic.net/MjAyMDExMDFfMTgy/MDAxNjA0MjI4ODc1NDMw.Ex906Mv9nnPEZGCh4SREknadZvzMO8LyDzGOHMKPdwAg.ZAmE6pU5lhEdeOUsPdxg8-gOuZrq_ipJ5VhqaViubI4g.JPEG.gambasg/%EC%9C%A0%ED%8A%9C%EB%B8%8C_%EA%B8%B0%EB%B3%B8%ED%94%84%EB%A1%9C%ED%95%84_%ED%95%98%EB%8A%98%EC%83%89.jpg?type=w800")
                .memberAuthority("GENERAL")
                .build();

        Member member = memberService.registMemberInfo(memberDto);

        Boolean result = memberService.withdrawMember(member.getMemberId());

        assertThat(result).isTrue();
    }

    @DisplayName("관리자가 교육생 해제를 요청하면 해당 계정의 권한은 GENERAL로 수정된다.")
    @Order(6)
    @Test
    void widthdrawTest() {
        MemberDto memberDto = MemberDto.builder()
                .memberNickname("김정윤")
                .memberEmail("indl1670@naver.com")
                .memberProfileImg("https://mblogthumb-phinf.pstatic.net/MjAyMDExMDFfMTgy/MDAxNjA0MjI4ODc1NDMw.Ex906Mv9nnPEZGCh4SREknadZvzMO8LyDzGOHMKPdwAg.ZAmE6pU5lhEdeOUsPdxg8-gOuZrq_ipJ5VhqaViubI4g.JPEG.gambasg/%EC%9C%A0%ED%8A%9C%EB%B8%8C_%EA%B8%B0%EB%B3%B8%ED%94%84%EB%A1%9C%ED%95%84_%ED%95%98%EB%8A%98%EC%83%89.jpg?type=w800")
                .memberAuthority("GENERAL")
                .build();

        Member member = memberService.registMemberInfo(memberDto);

        String authority = "GENERAL";
        MemberUpdateDto memberUpdateDto = MemberUpdateDto.builder().memberId(member.getMemberId()).memberAuthority(authority).build();
        String result = memberService.updateMemberInfoAdmin(memberUpdateDto).getMemberAuthority();

        assertThat(result).isEqualTo(authority);
    }

}
