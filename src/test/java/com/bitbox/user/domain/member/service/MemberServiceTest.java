package com.bitbox.user.domain.member.service;

import com.bitbox.user.dto.MemberDto;
import com.bitbox.user.dto.MemberUpdateDto;
import com.bitbox.user.service.MemberService;
import com.bitbox.user.domain.Member;
import com.bitbox.user.exception.DuplicationEmailException;
import com.bitbox.user.exception.InvalidMemberIdException;
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

    private MemberDto memberDto;
    private Member member;

    @BeforeEach
    public void before() {
        memberDto = MemberDto.builder()
                .memberNickname("김정윤")
                .memberEmail("indl1670@naver.com")
                .memberProfileImg("https://mblogthumb-phinf.pstatic.net/MjAyMDExMDFfMTgy/MDAxNjA0MjI4ODc1NDMw.Ex906Mv9nnPEZGCh4SREknadZvzMO8LyDzGOHMKPdwAg.ZAmE6pU5lhEdeOUsPdxg8-gOuZrq_ipJ5VhqaViubI4g.JPEG.gambasg/%EC%9C%A0%ED%8A%9C%EB%B8%8C_%EA%B8%B0%EB%B3%B8%ED%94%84%EB%A1%9C%ED%95%84_%ED%95%98%EB%8A%98%EC%83%89.jpg?type=w800")
                .memberAuthority("TRAINEE")
                .build();

        member =  memberService.registMemberInfo(memberDto);
    }

    @DisplayName("회원정보를 받아 회원DB에 등록한다.")
    @Order(1)
    @Test
    public void createMember() {
       //TODO: 등록 값과 결과값 비교
        // 값 전체 비교

    }

    @DisplayName("회원 등록 시 이미 등록된 이메일일 경우 예외가 발생한다.")
    @Order(2)
    @Test
    public void createMemberDuplicationEmail() throws DuplicationEmailException {
        assertThatThrownBy(() -> memberService.registMemberInfo(memberDto))
                .isInstanceOf(DuplicationEmailException.class)
                .hasMessage("이미 존재하는 계정입니다. 이메일을 확인해주세요");

    }

    @DisplayName("모든 회원은 본인의 정보를 조회할 수 있다.")
    @Order(3)
    @Test
    void getMyInfo() {
        Member memberInfo = memberService.getMyInfo(member.getMemberId());
        assertThat(memberInfo).isNotNull();
    }

    @DisplayName("유효하지 않은 회원 아이디로 조회 시 에러가 발생한다.")
    @Order(4)
    @Test
    void getMyInfoInvalidMemberId() {
        assertThatThrownBy(() -> memberService.getMyInfo("a"))
                .isInstanceOf(InvalidMemberIdException.class)
                .hasMessage("존재하지 않거나 유효하지 않은 회원정보입니다.");
    }

    @DisplayName("사용자 정보를 입력받아 해당하는 값만 수정한다.")
    @Order(5)
    @Test
    void updateTest() {
        MemberUpdateDto updateInfo = MemberUpdateDto.builder().memberNickname("수정완료").memberProfileImg("test.png").build();
        Member infoResult = memberService.updateMemberInfo(member.getMemberId(), updateInfo);

        assertThat(infoResult).isNotNull();
    }

    @DisplayName("회원 탈퇴를 요청하면 해당 계정의 삭제 여부는 true가 된다.")
    @Order(6)
    @Test
    void deleteTest() {
        Boolean result = memberService.withdrawMember(member.getMemberId());

        assertThat(result).isTrue();
    }

    // TODO: KafkaTest
//    @DisplayName("관리자가 교육생 해제를 요청하면 해당 계정의 권한은 GENERAL로 수정된다.")
//    @Order(7)
//    @Test
//    void widthdrawTest() {
//        MemberAuthorityDto memberInfo = MemberAuthorityUpdateDto.builder().memberId(member.getMemberId()).memberAuthority(member).build();
//        AuthorityType result = memberService.modifyMemberInfo(memberInfo);
//
//        assertThat(result).isEqualTo(AuthorityType.GENERAL);
//    }

}
