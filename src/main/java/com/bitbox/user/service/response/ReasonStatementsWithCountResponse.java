package com.bitbox.user.service.response;

import com.bitbox.user.domain.ReasonStatement;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ReasonStatementsWithCountResponse {
    List<ReasonStatementWithAttendanceAndMember> reasonStatements;
    Long totalCount;
}
