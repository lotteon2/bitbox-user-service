package com.bitbox.user.service;

import com.bitbox.user.dto.MemberUpdateDto;
import com.bitbox.user.repository.MemberInfoRepository;
import com.bitbox.user.service.response.MemberInfoResponse;
import com.bitbox.user.domain.Member;
import com.bitbox.user.dto.MemberAuthorityUpdateDto;
import com.bitbox.user.dto.MemberDto;
import com.bitbox.user.exception.DuplicationEmailException;
import com.bitbox.user.exception.InSufficientCreditException;
import com.bitbox.user.exception.InvalidMemberIdException;
import com.bitbox.user.service.response.MemberInfoWithCountResponse;
import io.github.bitbox.bitbox.dto.MemberCreditDto;
import io.github.bitbox.bitbox.dto.MemberPaymentDto;
import io.github.bitbox.bitbox.enums.AuthorityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberInfoRepository memberInfoRepository;

    public Member findByMemberId(String memberId) {
        return memberInfoRepository.findByMemberIdAndDeletedIsFalse(memberId).orElseThrow(() -> new InvalidMemberIdException("ERROR101 - 존재하지 않는 회원정보"));
    }

    /**
     * 회원가입
     * @param memberDto
     * @return MemberInfoRepository
     */
    @KafkaListener(topics = "${modifyTopic}")
    @Transactional
    public Member registMemberInfo(MemberDto memberDto) {
        // 회원 정보가 DB에 존재하는지 확인
        if (memberInfoRepository.countByMemberEmailAndDeletedIsFalse(memberDto.getMemberEmail()) != 0) {
            throw new DuplicationEmailException("ERROR100 - 중복 이메일 에러");
        }

        Member result = memberInfoRepository.save(Member.convertMemberDtoToMember(memberDto));
        return result;
    }

    /**
     * 회원정보 조회(일반 사용자)
     * memberId에 해당하는 사용자 정보 조회
     * @param memberId
     * @return MemberInfoResponse
     */
    public MemberInfoResponse getMyInfo(String memberId) {
        Member memberInfo = findByMemberId(memberId);

        return MemberInfoResponse.convertMemberToMemberInfoResponse(memberInfo);
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

        long totalCount = memberInfoRepository.count();

        return MemberInfoWithCountResponse.builder().memberInfoList(traineeList.getContent()).totalCount(totalCount).build();
    }

    /**
     * 회원정보 수정
     * @param memberId
     * @param memberUpdateDto
     * @return Member
     */
    @Transactional
    public Member updateMemberInfo(String memberId, MemberUpdateDto memberUpdateDto) {
        Member memberInfo = findByMemberId(memberId);

        return Member.convertMemberForUpdate(memberInfo, memberUpdateDto);
    }

    @Transactional
    public Member modifyMemberInfo(MemberAuthorityUpdateDto memberUpdateDto) {
        Member memberInfo = findByMemberId(memberUpdateDto.getMemberId());

        memberInfo.setMemberAuthority(AuthorityType.valueOf(memberUpdateDto.getMemberAuthority()));
        return memberInfoRepository.save(memberInfo);
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
        return memberInfoRepository.save(memberInfo).isDeleted();
    }


    /**
     * 채팅 시 크레딧 결제
     * @param memberCreditDto
     * @return
     */
    @Transactional
    public long useMyCredit(MemberCreditDto memberCreditDto) {
        Member memberInfo = findByMemberId(memberCreditDto.getMemberId());

        long remainCredit = memberInfo.getMemberCredit() - memberCreditDto.getCredit();

        if (remainCredit < 0) {
            throw new InSufficientCreditException("ERROR110 - 크레딧 부족");
        }

        memberInfo.setMemberCredit(memberInfo.getMemberCredit() - memberCreditDto.getCredit());
        return memberInfoRepository.save(memberInfo).getMemberCredit();
    }

    /**
     * 크레딧을 소모한 채팅 결제 실패 시 크레딧  되돌리기
     * @param memberCreditDto
     * @return
     */
    @KafkaListener(topics = "${creditTopic}")
    @Transactional
    public long getbackMyCredit(MemberCreditDto memberCreditDto) {
        Member memberInfo = findByMemberId(memberCreditDto.getMemberId());

        memberInfo.setMemberCredit(memberInfo.getMemberCredit() + memberCreditDto.getCredit());
        return memberInfoRepository.save(memberInfo).getMemberCredit();
    }
    /**
     * 크레딧 결제 후 크레딧 적립
     * @param memberPaymentDto
     * @return memberCredit
     */
    @KafkaListener(topics = "${kakaoTopic}")
    @Transactional
    public long addCredit(MemberPaymentDto memberPaymentDto) {
        Member memberInfo = memberInfoRepository.findByMemberIdAndDeletedIsFalse(memberPaymentDto.getMemberId()).orElseThrow(() -> new InvalidMemberIdException("ERROR101 - 존재하지 않는 회원정보"));

        memberInfo.setMemberCredit(memberInfo.getMemberCredit() + memberPaymentDto.getMemberCredit());
        return memberInfoRepository.save(memberInfo).getMemberCredit();
    }

}
