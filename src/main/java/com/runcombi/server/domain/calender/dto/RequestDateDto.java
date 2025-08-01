package com.runcombi.server.domain.calender.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestDateDto {
    private int year;
    private int month;
    private int day;
}
