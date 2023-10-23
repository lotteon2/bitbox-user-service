package com.bitbox.user.controller;


import com.bitbox.user.domain.ReasonStatement;
import com.bitbox.user.dto.ReasonStatementRegisterDto;
import com.bitbox.user.dto.ReasonStatementUpdateDto;
import com.bitbox.user.service.ReasonStatementService;
import com.bitbox.user.service.response.ReasonStatementsWithCountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class ReasonStatementController {
    private final ReasonStatementService reasonStatementService;

    /**
     * 사유서 등록
     */
    @PostMapping("/mypage/reason")
    public ResponseEntity<Void> registReasonStatement(@RequestHeader String memberId, @RequestBody ReasonStatementRegisterDto reasonStatementRegisterDto) {
        reasonStatementService.registReasonStatement(memberId, reasonStatementRegisterDto);

        return ResponseEntity.ok().build();
    }

    /**
     * 사유서 조회(관리자)
     */
    @GetMapping("/admin/reason/{classId}")
    public ResponseEntity<ReasonStatementsWithCountResponse> getAllReasonStatementsByClassId(@PathVariable Long classId, Pageable paging) {
        return ResponseEntity.ok(reasonStatementService.getReasonStatements(classId, paging));
    }

    /**
     * 사유서 읽음 처리
     */
    @PatchMapping("/admin/reason/detail/{reasonStatementId}")
    public ResponseEntity<ReasonStatement> modifyReasonStatementIsRead(@PathVariable Long reasonStatementId) {
        return ResponseEntity.ok(reasonStatementService.readReasonStatement(reasonStatementId));
    }

    /**
     * 사유서 상태 변경
     */
    @PatchMapping("/admin/reason/state/{reasonStatementId}")
    public ResponseEntity<Void> modifyReasonStatementState(@PathVariable Long reasonStatementId, @RequestBody ReasonStatementUpdateDto reasonStatementUpdateDto) {
        reasonStatementService.modifyReasonStatementState(reasonStatementId, reasonStatementUpdateDto);
        return ResponseEntity.ok().build();
    }
}
