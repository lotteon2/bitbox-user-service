package com.bixbox.user.repository;

import com.bixbox.user.domain.Member;
import com.bixbox.user.service.response.MemberInfoResponse;
import org.springframework.data.repository.CrudRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface MemberInfoRepository extends CrudRepository<Member, Long> {

    /**
     * 회원정보 조회(일반 회원)
     * @param memberId
     * @return MemberInfoResponse
     */
    MemberInfoResponse findByMemberId(String memberId);


    /**
     * 회원정보 조회(관리자)
     * @param classId
     * @param paging
     * @return MemberInfoResponse
     */
    MemberInfoResponse findByClassId(Long classId);
}
