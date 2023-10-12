package com.bitbox.user.service.response;

import com.bitbox.user.domain.Member;
import io.github.bitbox.bitbox.enums.AuthorityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MemberInfoResponse {
    private String memberNickname;
    private String memberEmail;
    private String memberProfileImg;
    private AuthorityType memberAuthority;
    private long memberCredit;

    // [TODO] 왜 안쓰지?
    public static MemberInfoResponse convertMemberToMemberInfoResponse(Member memberInfo) {
        return MemberInfoResponse.builder().memberNickname(memberInfo.getMemberNickname())
                .memberEmail(memberInfo.getMemberEmail())
                .memberProfileImg(memberInfo.getMemberProfileImg())
                .memberAuthority(memberInfo.getMemberAuthority())
                .memberCredit(memberInfo.getMemberCredit()).build();
    }
}
