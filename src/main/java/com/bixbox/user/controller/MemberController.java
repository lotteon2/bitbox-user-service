package com.bixbox.user.controller;

import com.bixbox.user.domain.Member;
import com.bixbox.user.dto.MemberAuthorityUpdateDto;
import com.bixbox.user.dto.MemberDto;
import com.bixbox.user.dto.MemberUpdateDto;
import com.bixbox.user.service.MemberService;
import com.bixbox.user.service.response.MemberInfoResponse;
import com.bixbox.user.service.response.MemberInfoWithCountResponse;
import io.github.bitbox.bitbox.dto.MemberCreditDto;
import io.github.bitbox.bitbox.enums.AuthorityType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<String> registMemberInfo(@Valid @RequestBody MemberDto memberDto) {
        return ResponseEntity.ok(memberService.registMemberInfo(memberDto).getMemberId());
    }

    /**
     * 회원정보 조회(일반 회원)
     */
    @GetMapping("/mypage")
    public ResponseEntity<MemberInfoResponse> getMyInfo(@RequestHeader String memberId) {
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
    public ResponseEntity<Member> updateMemberInfo(@RequestHeader String memberId, @RequestBody MemberUpdateDto memberUpdateDto) {
        return ResponseEntity.ok(memberService.updateMemberInfo(memberId, memberUpdateDto));
    }

    /**
     * 회원 정보 수정(관리자)
     */
    @PatchMapping("/admin")
    public ResponseEntity<AuthorityType> updateMemberInfo(@RequestBody MemberAuthorityUpdateDto memberAuthorityUpdateDto) {
        return ResponseEntity.ok(memberService.modifyMemberInfo(memberAuthorityUpdateDto).getMemberAuthority());
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
