package com.bixbox.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@Builder
@Getter
public class MemberDto {
    @NotEmpty(message = "필수 입력값입니다")
    @JsonProperty("member_nickname")
    private String memberNickname;
    @NotEmpty(message = "필수 입력값입니다")
    @JsonProperty("member_email")
    private String memberEmail;
    @NotEmpty(message = "필수 입력값입니다")
    @JsonProperty("member_profile_img")
    private String memberProfileImg;
    @NotEmpty(message = "필수 입력값입니다")
    @JsonProperty("member_authority")
    private String memberAuthority;
}
