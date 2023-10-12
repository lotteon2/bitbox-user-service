package com.bitbox.user.service.response;

import com.bitbox.user.domain.Member;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public class MemberInfoWithCountResponse {
    private List<Member> memberInfoList;
    private long totalCount;
}
