package com.bitbox.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.bitbox.bitbox.enums.ReasonStatementStatus;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="reason_statement")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReasonStatement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reason_statement_id")
    private Long reasonStatementId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id", nullable = false)
    private Attendance attendance;

    @Column(name = "reason_title", nullable = false)
    private String reasonTitle;

    @Column(name = "reason_contnet", nullable = false)
    private String reasonContent;

    @Column(name = "reason_attached_file")
    private String reasonAttachedFile;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'SUBMIT'")
    @Column(name = "reason_state", nullable = false, columnDefinition = "VARCHAR(255)")
    private ReasonStatementStatus reasonState;

    @ColumnDefault("false")
    @Column(name = "is_read", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean read;
}
