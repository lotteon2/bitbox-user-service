package com.bitbox.user.service.response;

import com.bitbox.user.domain.Member;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MemberInfoWithCountResponse {
    // [TODO] 진짜 멤버를 떨궈야하나?
    private List<Member> memberInfoList;
    private long totalCount;
}
