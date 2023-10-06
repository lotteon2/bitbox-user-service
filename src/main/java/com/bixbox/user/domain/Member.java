package com.bixbox.user.domain;

import com.bixbox.user.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="member")
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name="uuid2", strategy = "uuid2")
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
    private String memberNickname;

    @Column(name = "member_email", nullable = false)
    private String memberEmail;

    @Column(name = "member_profile_img", nullable = false)
    private String memberProfileImg;

    @ColumnDefault("0")
    @Column(name = "member_credit", nullable = false)
    private int memberCredit;

    @ColumnDefault("`GENERAL`")
    @Column(name = "member_authority", nullable = false)
    private String memberAuthority;

    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp default NOW()")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamp default NOW()")
    private LocalDateTime updatedAt;

    @ColumnDefault("false")
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    public static Member convertMemberDtoToMember(MemberDto memberDto) {
        return Member.builder()
                .memberName(memberDto.getMemberName())
                .memberNickname(memberDto.getMemberNickname())
                .memberEmail(memberDto.getMemberEmail())
                .memberProfileImg(memberDto.getMemberProfileImg())
                .memberAuthority(memberDto.getMemberAuthority())
                .build();
    }
}
