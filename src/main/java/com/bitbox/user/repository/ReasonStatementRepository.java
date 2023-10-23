package com.bitbox.user.repository;

import com.bitbox.user.domain.ReasonStatement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ReasonStatementRepository extends CrudRepository<ReasonStatement, String> {

    ReasonStatement findByReasonStatementId(Long reasonStatementId);

    @Query(value = "SELECT r FROM ReasonStatement r INNER JOIN Member m ON r.member.memberId = m.memberId WHERE m.classId = :classId ORDER BY r.reasonStatementId")
    Page<ReasonStatement> findAllByClassIdOrderByReasonStatementId(Long classId, Pageable paging);

    @Query(value = "SELECT COUNT(r) FROM ReasonStatement  r INNER JOIN Member m ON r.member.memberId = m.memberId WHERE m.classId = :classId")
    Long countByClassId(Long classId);
}
