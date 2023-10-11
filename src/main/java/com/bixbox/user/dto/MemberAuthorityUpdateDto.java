package com.bixbox.user.dto;

import io.github.bitbox.bitbox.enums.AuthorityType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberAuthorityUpdateDto {
    private String memberId;
    private AuthorityType memberAuthority;
}
