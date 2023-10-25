package com.bitbox.user.service;

import com.bitbox.user.domain.Member;
import com.bitbox.user.dto.MemberInfoUpdateDto;
import com.bitbox.user.dto.TraineeUpdateDto;
import com.bitbox.user.exception.DuplicationEmailException;
import com.bitbox.user.exception.InSufficientCreditException;
import com.bitbox.user.exception.InvalidMemberIdException;
import com.bitbox.user.repository.MemberInfoRepository;
import com.bitbox.user.service.response.MemberInfoWithCountResponse;
import io.github.bitbox.bitbox.dto.*;
import io.github.bitbox.bitbox.enums.AuthorityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberService {
    private final MemberInfoRepository memberInfoRepository;
    private final KafkaTemplate<String, MemberPaymentDto> kafkaTemplate;
    @Value("${cancelTopic}")
    private String cancelTopicName;

    private Member findByMemberId(String memberId) {
        return memberInfoRepository.findByMemberIdAndDeletedIsFalse(memberId).orElseThrow(() -> new InvalidMemberIdException("존재하지 않거나 유효하지 않은 회원정보입니다."));
    }

    /**
     * 회원가입
     * @param memberDto
     * @return MemberInfoRepository
     */
    @Transactional
    public Member registMemberInfo(MemberRegisterDto memberDto) {
        // 회원 정보가 DB에 존재하는지 확인
        if (memberInfoRepository.countByMemberEmailAndDeletedIsFalse(memberDto.getEmail()) != 0) {
            throw new DuplicationEmailException("이미 존재하는 계정입니다. 이메일을 확인해주세요.");
        }

        Member result = Member.builder()
                .memberId(memberDto.getId())
                .memberEmail(memberDto.getEmail())
                .memberNickname(memberDto.getName())
                .memberProfileImg(memberDto.getProfileImg())
                .memberAuthority(memberDto.getAuthority())
                .classId(memberDto.getClassId())
                .build();

        return memberInfoRepository.save(result);
    }

    /**
     * 교육생 정보 등록
     * @param memberId
     * @param traineeUpdateDto
     */
    @Transactional
    public void AddTraineeName(String memberId, TraineeUpdateDto traineeUpdateDto) {
        Member result = findByMemberId(memberId);
        result.setMemberName(traineeUpdateDto.getName());
        result.setClassId(traineeUpdateDto.getClassId());
    }

    /**
     * 회원정보 조회(일반 사용자)
     * memberId에 해당하는 사용자 정보 조회
     * @param memberId
     * @return Member
     */
    public Member getMyInfo(String memberId) {
        return findByMemberId(memberId);
    }

    /**
     * 회원정보 조회(관리자)
     * classId에 해당하는 사용자 리스트 조회
     * @param classId
     * @param paging
     * @return MemberInfoWithCountResponse
     */
    public MemberInfoWithCountResponse getTraineeInfo(Long classId, Pageable paging) {
        Page<Member> traineeList = memberInfoRepository.findAllByClassIdOrderByMemberNickname(classId, paging);
        return MemberInfoWithCountResponse.builder().memberInfoList(traineeList.getContent()).totalCount(memberInfoRepository.countMemberByClassId(classId)).build();
    }

    /**
     * 회원정보 수정
     * @param memberId
     * @param memberInfoUpdateDto
     * @return Member
     */
    @Transactional
    public Member updateMemberInfo(String memberId, MemberInfoUpdateDto memberInfoUpdateDto) {
        return memberInfoUpdateDto.CheckUpdatedInfo(findByMemberId(memberId), memberInfoUpdateDto);
    }

    /**
     * 회원 권한 변경
     * @param memberAuthorityDto
     * @return
     */
    @KafkaListener(topics = "${modifyTopic}")
    @Transactional
    public AuthorityType modifyMemberInfo(MemberAuthorityDto memberAuthorityDto) {
        Member memberInfo = findByMemberId(memberAuthorityDto.getMemberId());

        memberInfo.setMemberAuthority(memberAuthorityDto.getMemberAuthority());
        return memberInfo.getMemberAuthority();
    }

    /**
     * 회원 탈퇴
     * @param memberId
     * @return true
     */
    @Transactional
    public boolean withdrawMember(String memberId) {
        Member memberInfo = findByMemberId(memberId);

        memberInfo.setDeleted(true);
        return memberInfo.isDeleted();
    }


    /**
     * 채팅 시 크레딧 결제
     * @param memberCreditDto
     * @return
     */
    @Transactional
    public long useMyCredit(MemberCreditDto memberCreditDto) {
        Member memberInfo = findByMemberId(memberCreditDto.getMemberId());

        if ( memberInfo.getMemberCredit() - memberCreditDto.getCredit() < 0) {
            throw new InSufficientCreditException("크레딧이 부족합니다. 충전 후 이용해주세요.");
        }

        memberInfo.setMemberCredit(memberInfo.getMemberCredit() + memberCreditDto.getCredit());
        return memberInfo.getMemberCredit();
    }

    /**
     * 크레딧을 소모한 채팅 결제 실패 시 크레딧 되돌리기
     * @param memberCreditDto
     * @return
     */
    @KafkaListener(topics = "${creditTopic}")
    @Transactional
    public long getbackMyCredit(MemberCreditDto memberCreditDto) {
        Member memberInfo = findByMemberId(memberCreditDto.getMemberId());

        try {
            memberInfo.setMemberCredit(memberInfo.getMemberCredit() + memberCreditDto.getCredit());
        } catch (Exception e) {
            log.error("userCreditModifyTopic Error" + memberCreditDto);
            throw e;
        }

        return memberInfo.getMemberCredit();
    }

    /**
     * 크레딧 결제 후 크레딧 적립
     * @param memberPaymentDto
     * @return memberCredit
     */
    @KafkaListener(topics = "${kakaoTopic}")
    @Transactional
    public long addCredit(MemberPaymentDto memberPaymentDto) {
        Member memberInfo = findByMemberId(memberPaymentDto.getMemberId());

        try {
            memberInfo.setMemberCredit(memberInfo.getMemberCredit() + memberPaymentDto.getMemberCredit());
        } catch (Exception e) {
            log.error("kakaoMemberCreditTopic Error" + memberPaymentDto);
            kafkaTemplate.send(cancelTopicName, memberPaymentDto);
            throw e;
        }

        return memberInfo.getMemberCredit();
    }


    /**
     * 교육생 유효성 검사
     * @param memberValidDto
     * @return
     */
    public MemberTraineeResult checkMemberValid(List<MemberValidDto> memberValidDto) {
        List<MemberValidDto> validMember = new ArrayList<>();
        List<MemberValidDto> invalidMember = new ArrayList<>();

        for (MemberValidDto memberInfo : memberValidDto) {
            Optional<Member> result = memberInfoRepository.findByMemberId(memberInfo.getMemberId());

            if (result.isEmpty()) {
                invalidMember.add(memberInfo);
                continue;
            }

            Member member = result.get();

            if (member.isDeleted() || !member.getClassId().equals(memberInfo.getClassId())) {
                invalidMember.add(memberInfo);
            } else {
                validMember.add(memberInfo);
            }
        }


        return MemberTraineeResult.builder().validMember(validMember).invalidMember(invalidMember).build();
    }

    @Transactional
    public void modifyAuthorityByClassId(Long classId, AuthorityType type) {
        List<Member> traineeList = memberInfoRepository.findAllByClassId(classId);

        for (Member trainee: traineeList) {
            try {
                trainee.setMemberAuthority(type);
                trainee.setClassId(null);
            } catch (Exception e) {
                log.error("adminMemberBoardTopic Error" + trainee.getMemberId());
                throw e;
            }

        }
    }
}
