package com.bixbox.user.domain;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.mapping.Join;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "reason_statement")
@DynamicInsert
public class ReasonStatement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reason_statement_id")
    private Long reasonStatementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id", nullable = false)
    private Attendance attendance;

    @OneToOne(mappedBy = "reasonStatement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RejectReason rejectReason;

    @Column(name = "reason_title", nullable = false)
    private String reasonTitle;

    @Column(name = "reason_contnet", nullable = false)
    private String reasonContent;

    @Column(name = "reason_attached_file")
    private String reasonAttachedFile;

    @Column(name = "reason_state", nullable = false)
    private boolean reasonState;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;
}
