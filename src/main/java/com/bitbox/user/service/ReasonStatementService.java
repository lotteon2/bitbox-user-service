package com.bitbox.user.service;

import com.bitbox.user.domain.Attendance;
import com.bitbox.user.domain.ReasonStatement;
import com.bitbox.user.domain.RejectReason;
import com.bitbox.user.dto.ReasonStatementRegisterDto;
import com.bitbox.user.dto.ReasonStatementUpdateDto;
import com.bitbox.user.repository.AttendanceRepository;
import com.bitbox.user.repository.MemberInfoRepository;
import com.bitbox.user.repository.ReasonStatementRepository;
import com.bitbox.user.repository.RejectReasonRepository;
import com.bitbox.user.service.response.ReasonStatementWithAttendanceAndMember;
import com.bitbox.user.service.response.ReasonStatementsWithCountResponse;
import io.github.bitbox.bitbox.enums.ReasonStatementStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReasonStatementService {
    private final ReasonStatementRepository reasonStatementRepository;
    private final AttendanceRepository attendanceRepository;
    private final MemberInfoRepository memberInfoRepository;
    private final RejectReasonRepository rejectReasonRepository;

    /**
     * 사유서 등록
     * @param reasonStatementRegisterDto
     * @return
     */
    @Transactional
    public ReasonStatement registReasonStatement(String memberId, ReasonStatementRegisterDto reasonStatementRegisterDto) {
        return checkReasonStatement(memberId, reasonStatementRegisterDto);
    }

    /**
     * 사유서 조회(관리자)
     * @param classId
     * @param paging
     * @return
     */
    public ReasonStatementsWithCountResponse getReasonStatements(Long classId, Pageable paging) {
        Page<ReasonStatementWithAttendanceAndMember> statementList = reasonStatementRepository.findAllByClassIdOrderByReasonStatementId(classId, paging);

        return ReasonStatementsWithCountResponse.builder().reasonStatements(statementList.getContent()).totalCount(reasonStatementRepository.countByClassId(classId)).build();
    }


    /**
     * 사유서 읽음 처리 및 상세 조회
     * @param reasonStatementId
     */
    @Transactional
    public ReasonStatement readReasonStatement(Long reasonStatementId) {
        ReasonStatement result = reasonStatementRepository.findByReasonStatementId(reasonStatementId);
        result.setRead(true);
        
        return result;
    }

    /**
     * 사유서 상태 수정
     * @param reasonStatementId
     * @param reasonStatementUpdateDto
     */
    @Transactional
    public void modifyReasonStatementState(Long reasonStatementId, ReasonStatementUpdateDto reasonStatementUpdateDto) {
        ReasonStatement result = reasonStatementRepository.findByReasonStatementId(reasonStatementId);
        result.setReasonState(reasonStatementUpdateDto.getReasonState());

        if (reasonStatementUpdateDto.getReasonState() == ReasonStatementStatus.APPROVE) return;

        RejectReason test = RejectReason.builder().reasonStatementId(reasonStatementId).reasonStatement(result).rejectReason(reasonStatementUpdateDto.getRejectReason()).build();
        rejectReasonRepository.save(test);
    }

    private ReasonStatement checkReasonStatement(String memberId, ReasonStatementRegisterDto reasonStatementRegisterDto) {
        ReasonStatement statement = reasonStatementRepository.findByAttendance_AttendanceId(reasonStatementRegisterDto.getAttendanceId());

        // 신규면 등록
        if (statement == null) {
            ReasonStatement result = ReasonStatement.builder()
                    .attendance(attendanceRepository.findByAttendanceId(reasonStatementRegisterDto.getAttendanceId()))
                    .reasonTitle(reasonStatementRegisterDto.getReasonTitle())
                    .reasonContent(reasonStatementRegisterDto.getReasonContent())
                    .reasonAttachedFile(reasonStatementRegisterDto.getReasonAttachedFile())
                    .member(memberInfoRepository.findByMemberIdAndDeletedIsFalse(memberId).orElseThrow())
                    .build();

            return reasonStatementRepository.save(result);
        }

        // 이미 존재하면 수정
        statement.setReasonTitle(reasonStatementRegisterDto.getReasonTitle());
        statement.setReasonContent(reasonStatementRegisterDto.getReasonContent());
        statement.setReasonAttachedFile(reasonStatementRegisterDto.getReasonAttachedFile());
        return statement;
    }
}
