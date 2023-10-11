package com.bixbox.user.domain;

import com.bixbox.user.dto.MemberAuthorityUpdateDto;
import com.bixbox.user.dto.MemberDto;
import com.bixbox.user.dto.MemberUpdateDto;
import io.github.bitbox.bitbox.dto.MemberAuthorityDto;
import io.github.bitbox.bitbox.enums.AuthorityType;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="member")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Member {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name="uuid2", strategy = "uuid2")
    @Column(name="member_id")
    private String memberId;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attendance> attendances;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReasonStatement> reasonStatements;

    @Column(name = "class_id")
    private Long classId;

    @Column(name = "member_nickname", nullable = false)
    private String memberNickname;

    @Column(name = "member_email", nullable = false)
    private String memberEmail;

    @Column(name = "member_profile_img", nullable = false, columnDefinition = "LONGTEXT")
    private String memberProfileImg;

    @ColumnDefault("0")
    @Column(name = "member_credit", nullable = false)
    private long memberCredit;

    @Column(name = "member_authority", nullable = false)
    private AuthorityType memberAuthority;

    @CreatedDate
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @ColumnDefault("false")
    @Column(name = "deleted", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean deleted;

    public static Member testMember(MemberAuthorityDto memberDto) {
        return Member.builder()
                .memberEmail("test@naver.com")
                .memberNickname("test")
                .memberProfileImg("test.jpg")
                .memberId(memberDto.getMemberId())
                .memberAuthority(memberDto.getMemberAuthority())
                .build();
    }
    public static Member convertMemberDtoToMember(MemberDto memberDto) {
        return Member.builder()
                .memberNickname(memberDto.getMemberNickname())
                .memberEmail(memberDto.getMemberEmail())
                .memberProfileImg(memberDto.getMemberProfileImg())
                .memberAuthority(memberDto.getMemberAuthority())
                .build();
    }

    public static Member convertMemberForUpdate(Member original, MemberUpdateDto update) {
        if (update.getMemberNickname() != null) original.setMemberNickname(update.getMemberNickname());
        if (update.getMemberProfileImg() != null) original.setMemberProfileImg(update.getMemberProfileImg());

        return original;
    }


}
