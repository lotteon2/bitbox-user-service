package com.bitbox.user.repository;

import com.bitbox.user.domain.Member;
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
     * 관리자 전체 학생 조회
     */
    @Query(value = "SELECT m FROM Member m WHERE m.memberId = :memberId")
    Member findByMemberIdForAdmin(String memberId);

    Optional<Member> findByMemberId(String memberId);

    /**
     * 회원정보 조회(관리자)
     * @param classId
     * @return MemberInfoResponse
     */
    @Query(value = "SELECT m FROM Member m WHERE m.classId = :classId AND m.memberAuthority = 'TRAINEE' ORDER BY m.memberName")
    Page<Member> findAllByClassIdOrderByMemberNickname(Long classId, Pageable paging);


    /**
     * 회원정보 조회 전체 학생 수
     */
    @Query(value = "SELECT count(m) FROM Member m WHERE m.classId = :classId AND m.memberAuthority = 'TRAINEE'")
    Long countMemberByClassId(Long classId);

    /**
     * 반 삭제 카프카
     * @param classId
     * @return
     */
    List<Member> findAllByClassId(Long classId);

    @Query(value = "SELECT m FROM Member m WHERE m.classId = :classId AND m.memberAuthority = 'TRAINEE'")
    List<Member> findAllTraineeByClassId(Long classId);
//    List<Member> findAllAndDeletedIsFalse();

}
