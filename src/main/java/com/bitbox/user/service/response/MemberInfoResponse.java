package com.bitbox.user.service.response;

import com.bitbox.user.domain.Member;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.github.bitbox.bitbox.enums.AuthorityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY) // TODO 이거 떼도 돌아가지 않나?
public class MemberInfoResponse {
    private String memberNickname;
    private String memberEmail;
    private String memberProfileImg;
    private AuthorityType memberAuthority;
    private long memberCredit;

    public static MemberInfoResponse convertMemberToMemberInfoResponse(Member memberInfo) {
        return MemberInfoResponse.builder().memberNickname(memberInfo.getMemberNickname())
                .memberEmail(memberInfo.getMemberEmail())
                .memberProfileImg(memberInfo.getMemberProfileImg())
                .memberAuthority(memberInfo.getMemberAuthority())
                .memberCredit(memberInfo.getMemberCredit()).build();
    }
}
