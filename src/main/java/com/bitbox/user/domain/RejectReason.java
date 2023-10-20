package com.bitbox.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Table(name = "reject_reason")
@DynamicInsert
public class RejectReason {
    @Id
    private Long id;

    @JsonIgnore
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reason_statement_id", nullable = false)
    private ReasonStatement reasonStatement;

    @Column(name = "reject_reason", nullable = false)
    private String rejectReason;

}
