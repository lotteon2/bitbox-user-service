package com.bitbox.user.dto;

import com.bitbox.user.domain.Member;
import io.github.bitbox.bitbox.enums.AuthorityType;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Builder
@Getter
public class MemberDto {
    private String memberId;
    @NotEmpty(message = "필수 입력값입니다")
    private String memberNickname;
    @NotEmpty(message = "필수 입력값입니다")
    private String memberEmail;
    private String memberProfileImg;
    @NotNull(message = "필수 입력값입니다")
    private AuthorityType memberAuthority;
    private Long classId;

    public static Member convertMemberDtoToMember(MemberDto memberDto) {
        return Member.builder()
                .memberNickname(memberDto.getMemberNickname())
                .memberEmail(memberDto.getMemberEmail())
                .memberProfileImg(memberDto.getMemberProfileImg())
                .memberAuthority(memberDto.getMemberAuthority())
                .classId(memberDto.getClassId())
                .build();
    }
}
