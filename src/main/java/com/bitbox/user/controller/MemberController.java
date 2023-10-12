package com.bitbox.user.controller;
import com.bitbox.user.dto.MemberUpdateDto;
import com.bitbox.user.domain.Member;
import com.bitbox.user.dto.MemberDto;
import com.bitbox.user.service.MemberService;
import com.bitbox.user.service.response.MemberInfoWithCountResponse;
import io.github.bitbox.bitbox.dto.MemberAuthorityDto;
import io.github.bitbox.bitbox.dto.MemberCreditDto;
import io.github.bitbox.bitbox.enums.AuthorityType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@CrossOrigin("*") // [TODO] 일단 컨트롤러에 붙이기는 했는데 확인 필요.
public class MemberController {
    private final MemberService memberService;

    @GetMapping("")
    public ResponseEntity<Member> getAll() {
        return ResponseEntity.ok(memberService.getMyInfo("4e5ccba9-5512-46e4-8095-eaffc42a633b")); // [TODO] 뭐야 이 프리픽스는?
    }
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
    public ResponseEntity<Member> updateMemberInfo(@RequestHeader String memberId, @Valid @RequestBody MemberUpdateDto memberUpdateDto) {
        return ResponseEntity.ok(memberService.updateMemberInfo(memberId, memberUpdateDto));
    }

    /**
     * 회원 권한 수정(관리자)
     */
    @PatchMapping("/admin")
    public ResponseEntity<AuthorityType> updateMemberInfo(@Valid @RequestBody MemberAuthorityDto memberAuthorityDto) {
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
    public ResponseEntity<Long> useMyCredit(@Valid @RequestBody MemberCreditDto memberCreditDto) { // [TODO] 나 여기다가 체크하는 어노테이션이 없는데 왠 Valid?
        return ResponseEntity.ok(memberService.useMyCredit(memberCreditDto));
    }


}
