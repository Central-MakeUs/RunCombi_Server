package com.runcombi.server.domain.calender.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MonthRunDto {
    private String date;           // "yyyyMMdd"
    private List<Long> runId;
}
