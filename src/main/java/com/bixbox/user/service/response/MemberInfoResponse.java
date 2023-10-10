package com.bixbox.user.service.response;

import com.bixbox.user.domain.Member;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MemberInfoResponse {
    private String memberNickname;
    private String memberEmail;
    private String memberProfileImg;
    private String memberAuthority;
    private int memberCredit;

    public static MemberInfoResponse convertMemberToMemberInfoResponse(Member memberInfo) {
        return MemberInfoResponse.builder().memberNickname(memberInfo.getMemberNickname())
                .memberEmail(memberInfo.getMemberEmail())
                .memberProfileImg(memberInfo.getMemberProfileImg())
                .memberAuthority(memberInfo.getMemberAuthority())
                .memberCredit(memberInfo.getMemberCredit()).build();
    }
}
