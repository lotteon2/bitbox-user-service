package com.bitbox.user.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberUpdateDto {
    private String memberNickname;
    private String memberProfileImg;
}
