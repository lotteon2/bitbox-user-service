package com.bixbox.user.repository;

import com.bixbox.user.domain.Member;
import com.bixbox.user.dto.MemberUpdateDto;
import com.bixbox.user.service.response.MemberInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface MemberInfoRepository extends CrudRepository<Member, String> {


    /**
     * 회원가입 전 이메일 여부 확인
     * @param memberEmail
     * @return int
     */
    int countByMemberEmailAndDeletedIsFalse(String memberEmail);

    /**
     * 회원정보 조회(일반 회원)
     * @param memberId
     * @return MemberInfoResponse
     */
    Optional<Member> findByMemberIdAndDeletedIsFalse(String memberId);


    /**
     * 회원정보 조회(관리자)
     * @param classId
     * @return MemberInfoResponse
     */
    Page<Member> findAllByClassIdOrderByMemberNickname(Long classId, Pageable paging);

//    List<Member> findAllAndDeletedIsFalse();

}
