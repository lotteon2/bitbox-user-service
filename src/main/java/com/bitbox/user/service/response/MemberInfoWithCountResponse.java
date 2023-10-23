package com.bitbox.user.service.response;

import com.bitbox.user.domain.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MemberInfoWithCountResponse {
    private List<Member> memberInfoList;
    private long totalCount;
}
