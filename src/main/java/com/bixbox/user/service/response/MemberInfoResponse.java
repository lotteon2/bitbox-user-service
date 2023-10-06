package com.bixbox.user.service.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;

@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MemberInfoResponse {
    private String memberName;
    private String memberNickname;
    private String memberEmail;
    private String memberProfileImg;
    private int memberCredit;
}
