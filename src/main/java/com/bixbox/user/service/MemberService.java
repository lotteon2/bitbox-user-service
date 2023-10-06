package com.bixbox.user.service;

import com.bixbox.user.domain.Member;
import com.bixbox.user.dto.MemberDto;
import com.bixbox.user.repository.MemberInfoRepository;
import com.bixbox.user.service.response.MemberInfoResponse;
import com.bixbox.user.service.response.MemberInfoWithCountResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberInfoRepository memberInfoRepository;
    private static final int SIZE = 8;

    /**
     * 회원가입
     * @param memberDto
     * @return MemberInfoRepository
     */
    public Member registMemberInfo(MemberDto memberDto) {
        return memberInfoRepository.save(Member.convertMemberDtoToMember(memberDto));
    }

    /**
     * 회원정보 조회(일반 사용자)
     * memberId에 해당하는 사용자 정보 조회
     * @param memberId
     * @return MemberInfoResponse
     */
    public MemberInfoResponse getMyInfo(String memberId) {
        return memberInfoRepository.findByMemberId(memberId);
    }

    /**
     * 회원정보 조회(관리자)
     * classId에 해당하는 사용자 리스트 조회
     * @param classIdList
     * @param page
     * @return MemberInfoWithCountResponse
     */
    public MemberInfoWithCountResponse getTraineeInfo(List<Long> classIdList, int page) {
        Pageable paging = (Pageable) PageRequest.of(page, SIZE);

        List<MemberInfoResponse> traineeList = new ArrayList<>();

        // ClassId에 해당하는 교육생 정보 저장
        // limit offset 적용
        for (Long classId: classIdList) {
            traineeList.add(memberInfoRepository.findByClassId(classId));
        }

        long totalCount = memberInfoRepository.count();

        return new MemberInfoWithCountResponse(traineeList, totalCount);
    }

}
