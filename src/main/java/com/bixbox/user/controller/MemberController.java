package com.bixbox.user.controller;

import com.bixbox.user.domain.Member;
import com.bixbox.user.dto.MemberDto;
import com.bixbox.user.service.MemberService;
import com.bixbox.user.service.response.MemberInfoResponse;
import com.bixbox.user.service.response.MemberInfoWithCountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    /**
     * 회원가입
     */
    @PostMapping("signup")
    public ResponseEntity<Member> regustMemberInfo(@RequestBody MemberDto memberDto) {
        return ResponseEntity.ok(memberService.registMemberInfo(memberDto));
    }

    /**
     * 회원정보 조회(일반 회원)
     */
    @GetMapping("mypage")
    public ResponseEntity<MemberInfoResponse> getMyInfo(String memberId) {
        return ResponseEntity.ok(memberService.getMyInfo(memberId));
    }

    /**
     * 교육생 정보 조회(관리자)
     */
    // TODO: Pagination 처리한거 이게 맞는지 확인 요망
    @GetMapping("admin")
    public ResponseEntity<MemberInfoWithCountResponse> getTraineeInfo(@PathVariable("classId") List<Long> classId, @PathVariable("page") int page) {
        return ResponseEntity.ok(memberService.getTraineeInfo(classId, page));
    }
}
