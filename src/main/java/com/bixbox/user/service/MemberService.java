package com.bixbox.user.service;

import com.bixbox.user.domain.Member;
import com.bixbox.user.dto.MemberDto;
import com.bixbox.user.dto.MemberUpdateDto;
import com.bixbox.user.exception.DuplicationEmailException;
import com.bixbox.user.exception.InvalidMemberIdException;
import com.bixbox.user.repository.MemberInfoRepository;
import com.bixbox.user.service.response.MemberInfoResponse;
import com.bixbox.user.service.response.MemberInfoWithCountResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberInfoRepository memberInfoRepository;

    /**
     * 회원가입
     * @param memberDto
     * @return MemberInfoRepository
     */
    public Member registMemberInfo(MemberDto memberDto) {
        // 회원 정보가 DB에 존재하는지 확인
        if (memberInfoRepository.countByMemberEmailAndDeletedIsFalse(memberDto.getMemberEmail()) != 0) {
            throw new DuplicationEmailException("ERROR100 - 중복 이메일 에러");
        }

        return memberInfoRepository.save(Member.convertMemberDtoToMember(memberDto));
    }

    /**
     * 회원정보 조회(일반 사용자)
     * memberId에 해당하는 사용자 정보 조회
     * @param memberId
     * @return MemberInfoResponse
     */
    public MemberInfoResponse getMyInfo(String memberId) {
        Member memberInfo = memberInfoRepository.findByMemberIdAndDeletedIsFalse(memberId);

        if (memberInfo == null) {
            throw new InvalidMemberIdException("ERROR101 - 존재하지 않는 회원정보");
        }
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
    public Member updateMemberInfo(String memberId, MemberUpdateDto memberUpdateDto) {
        Member memberInfo = memberInfoRepository.findByMemberIdAndDeletedIsFalse(memberId);

        if (memberInfo == null) throw new InvalidMemberIdException("ERROR101 - 존재하지 않는 회원정보");
        if (memberUpdateDto.getMemberNickname() != null) memberInfo.setMemberNickname(memberUpdateDto.getMemberNickname());
        if (memberUpdateDto.getMemberProfileImg() != null) memberInfo.setMemberProfileImg(memberUpdateDto.getMemberProfileImg());
        if (memberUpdateDto.getMemberAuthority() != null) memberInfo.setMemberAuthority(memberUpdateDto.getMemberAuthority());
        return memberInfoRepository.save(memberInfo);
    }

    /**
     * 회원 탈퇴
     * @param memberId
     * @return true
     */
    public boolean withdrawMember(String memberId) {
        Member memberInfo = memberInfoRepository.findByMemberIdAndDeletedIsFalse(memberId);

        memberInfo.setDeleted(true);
        return memberInfoRepository.save(memberInfo).isDeleted();
    }

    public Member updateMemberInfoAdmin(MemberUpdateDto memberUpdateDto) {
        Member memberInfo = memberInfoRepository.findByMemberIdAndDeletedIsFalse(memberUpdateDto.getMemberId());

        if (memberInfo == null) throw new InvalidMemberIdException("ERROR101 - 존재하지 않는 회원정보");
        if (memberUpdateDto.getMemberAuthority() != null) memberInfo.setMemberAuthority(memberUpdateDto.getMemberAuthority());
        return memberInfoRepository.save(memberInfo);

    }

}
