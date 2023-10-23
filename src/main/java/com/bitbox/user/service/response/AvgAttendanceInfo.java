package com.bitbox.user.service.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
public class AvgAttendanceInfo {
    private LocalDate name;
    private Long num;
}
