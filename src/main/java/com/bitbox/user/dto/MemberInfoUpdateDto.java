package com.bitbox.user.dto;

import com.bitbox.user.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberInfoUpdateDto {
    private String memberNickname;
    private String memberProfileImg;

    public Member CheckUpdatedInfo(Member before, MemberInfoUpdateDto after) {
        if (after.getMemberNickname() != null) before.setMemberNickname(after.getMemberNickname());
        if (after.getMemberProfileImg() != null) before.setMemberProfileImg(after.getMemberProfileImg());

        return before;
    }
}
