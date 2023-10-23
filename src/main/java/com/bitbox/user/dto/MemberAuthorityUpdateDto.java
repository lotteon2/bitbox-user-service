package com.bitbox.user.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberAuthorityUpdateDto {
    private String memberId;
    private String memberAuthority;
}
