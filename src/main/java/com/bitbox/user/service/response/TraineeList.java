package com.bitbox.user.service.response;

import com.bitbox.user.dto.MemberValidDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class TraineeList {
    private List<MemberValidDto> validMember;
    private List<MemberValidDto> invalidMember;

    public static TraineeList convertLists(List<MemberValidDto> valid, List<MemberValidDto> invalid) {
        return TraineeList.builder().validMember(valid).invalidMember(invalid).build();
    }
}
