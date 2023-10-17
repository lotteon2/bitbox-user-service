package com.bitbox.user.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberValidDto {
    private String memberId;
    private Long classId;
}
