package com.bitbox.user.controller;
import com.bitbox.user.domain.Member;
import com.bitbox.user.dto.MemberInfoUpdateDto;
import com.bitbox.user.dto.MemberValidDto;
import com.bitbox.user.service.MemberService;
import com.bitbox.user.service.response.MemberInfoWithCountResponse;
import com.bitbox.user.service.response.TraineeList;
import io.github.bitbox.bitbox.dto.MemberAuthorityDto;
import io.github.bitbox.bitbox.dto.MemberCreditDto;
import io.github.bitbox.bitbox.dto.MemberRegisterDto;
import io.github.bitbox.bitbox.enums.AuthorityType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    /**
     * 회원가입
     */
//    @KafkaListener(topics = "")
    @PostMapping("/signup")
    public ResponseEntity<String> registMemberInfo(@Valid @RequestBody MemberRegisterDto memberDto) {
        return ResponseEntity.ok(memberService.registMemberInfo(memberDto).getMemberId());
    }

    /**
     * 교육생 이름 등록
     */
    @PatchMapping("/name")
    public ResponseEntity<Void> registTraineeName(@RequestHeader String memberId, @RequestBody String memberName) {
        memberService.AddTraineeName(memberId, memberName);
        return ResponseEntity.ok().build();
    }

    /**
     * 교육생 유효성 검사
     */
    @GetMapping("/admin/check")
    public ResponseEntity<TraineeList> checkMemberValid(@RequestBody List<MemberValidDto> memberValidDto) {
        return ResponseEntity.ok(memberService.checkMemberValid(memberValidDto));
    }

    /**
     * 회원정보 조회(일반 회원)
     */
    @GetMapping("/mypage")
    public ResponseEntity<Member> getMyInfo(@RequestHeader String memberId) {
        return ResponseEntity.ok(memberService.getMyInfo(memberId));
    }

    /**
     * 교육생 정보 조회(관리자)
     */
    @GetMapping("/admin/{classId}")
    public ResponseEntity<MemberInfoWithCountResponse> getTraineeInfo(@PathVariable Long classId, Pageable paging) {
        return ResponseEntity.ok(memberService.getTraineeInfo(classId, paging));
    }

    /**
     * 내 정보 수정
     */
    @PatchMapping("/mypage")
    public ResponseEntity<Member> updateMemberInfo(@RequestHeader String memberId, @RequestBody MemberInfoUpdateDto memberInfoUpdateDto) {
        return ResponseEntity.ok(memberService.updateMemberInfo(memberId, memberInfoUpdateDto));
    }

    /**
     * 회원 권한 수정(관리자)
     */
    @PatchMapping("/admin")
    public ResponseEntity<AuthorityType> updateMemberInfo(@RequestBody MemberAuthorityDto memberAuthorityDto) {
        return ResponseEntity.ok(memberService.modifyMemberInfo(memberAuthorityDto));
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/mypage")
    public ResponseEntity<Boolean> widthdrawMember(@RequestHeader String memberId) {
        return ResponseEntity.ok(memberService.withdrawMember(memberId));
    }


    /**
     * 크레딧 소모
     */
    @PatchMapping("/credit")
    public ResponseEntity<Long> useMyCredit(@RequestBody MemberCreditDto memberCreditDto) {
        return ResponseEntity.ok(memberService.useMyCredit(memberCreditDto));
    }


}
