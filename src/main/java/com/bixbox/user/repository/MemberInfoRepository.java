package com.bixbox.user.repository;

import com.bixbox.user.domain.Member;
import com.bixbox.user.service.response.MemberInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface MemberInfoRepository extends CrudRepository<Member, String> {


    /**
     * 회원가입 전 이메일 여부 확인
     * @param memberEmail
     * @return int
     */
//    @Query("SELECT count(mem.memberEmail) FROM  Member mem WHERE mem.memberEmail = :memberEmail AND mem.isDeleted = false")
    int countByMemberEmailAndDeletedIsFalse(String memberEmail);

    /**
     * 회원정보 조회(일반 회원)
     * @param memberId
     * @return MemberInfoResponse
     */
//    @Query("SELECT mem FROM  Member mem WHERE mem.memberId = :memberId AND mem.isDeleted = false ")
    Member findByMemberIdAndDeletedIsFalse(String memberId);


    /**
     * 회원정보 조회(관리자)
     * @param classId
     * @return MemberInfoResponse
     */
    Page<Member> findAllByClassId(Long classId, Pageable paging);

}
