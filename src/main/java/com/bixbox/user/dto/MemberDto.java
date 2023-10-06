package com.bixbox.user.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberDto {
    private String memberName;
    private String memberNickname;
    private String memberEmail;
    private String memberProfileImg;
    private String memberAuthority;
}