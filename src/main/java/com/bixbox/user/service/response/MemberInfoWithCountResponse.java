package com.bixbox.user.service.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MemberInfoWithCountResponse {
    private List<MemberInfoResponse> memberInfoList;
    private long totalCount;

    public MemberInfoWithCountResponse(List<MemberInfoResponse> memberInfoList, long totalCount) {
        this.memberInfoList = memberInfoList;
        this.totalCount = totalCount;
    }
}
