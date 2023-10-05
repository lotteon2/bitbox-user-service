package com.bixbox.user.domain;

import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="member")
@DynamicInsert
public class Member {
    @Id
    @Column(name="member_id")
    private String memberId;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Attendance> attendances;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ReasonStatement> reasonStatements;

    @Column(name = "class_id")
    private Long classId;

    @Column(name = "member_name")
    private String memberName;

    @Column(name = "member_nickname", nullable = false)
    private String memberNickName;

    @Column(name = "member_email", nullable = false)
    private String memberEmail;

    @Column(name = "member_profile_img", nullable = false)
    private String memberProfileImg;

    @Column(name = "member_credit", nullable = false)
    private int memberCredit;

    @Column(name = "member_authority", nullable = false)
    private String memberAuthority;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}
