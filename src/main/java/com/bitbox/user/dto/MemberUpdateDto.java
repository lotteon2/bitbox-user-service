package com.bitbox.user.dto;

import com.bitbox.user.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberUpdateDto {
    private String memberNickname;
    private String memberProfileImg;

    public static Member convertMemberForUpdate(Member original, MemberUpdateDto update) {
        if (update.getMemberNickname() != null) original.setMemberNickname(update.getMemberNickname());
        if (update.getMemberProfileImg() != null) original.setMemberProfileImg(update.getMemberProfileImg());

        return original;
    }
}
