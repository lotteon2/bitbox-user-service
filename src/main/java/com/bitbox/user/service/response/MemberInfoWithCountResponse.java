package com.bitbox.user.service.response;

import com.bitbox.user.domain.Member;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MemberInfoWithCountResponse {
    private List<Member> memberInfoList;
    @JsonProperty("total_count") // TODO 이건 왜 안뗄까 친구야?
    private long totalCount;
}
