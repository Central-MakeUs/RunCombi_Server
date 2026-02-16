package com.runcombi.server.admin.service;

import com.runcombi.server.admin.dto.UsageStatsBucketType;
import com.runcombi.server.admin.dto.UsageStatsPointDto;
import com.runcombi.server.admin.dto.UsageStatsResponseDto;
import com.runcombi.server.domain.run.entity.Run;
import com.runcombi.server.domain.run.repository.RunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminStatisticsService {
    private final RunRepository runRepository;

    public UsageStatsResponseDto getRunUsageStats(
            UsageStatsBucketType bucketType,
            LocalDate startDate,
            LocalDate endDate
    ) {
        UsageStatsBucketType safeBucketType = bucketType == null ? UsageStatsBucketType.WEEK : bucketType;
        LocalDate today = LocalDate.now();

        LocalDate resolvedStartDate;
        LocalDate resolvedEndDate;

        if (startDate != null && endDate != null) {
            resolvedStartDate = startDate;
            resolvedEndDate = endDate;
        } else {
            resolvedEndDate = today;
            resolvedStartDate = switch (safeBucketType) {
                case DAY -> today.minusDays(6);        // 최근 7일
                case WEEK -> today.minusWeeks(11);     // 최근 12주
                case MONTH -> today.minusMonths(11);   // 최근 12개월
                case YEAR -> today.minusYears(4);      // 최근 5년
            };
        }

        if (resolvedStartDate.isAfter(resolvedEndDate)) {
            LocalDate temp = resolvedStartDate;
            resolvedStartDate = resolvedEndDate;
            resolvedEndDate = temp;
        }

        LocalDateTime startDateTime = resolvedStartDate.atStartOfDay();
        LocalDateTime endDateTimeExclusive = resolvedEndDate.plusDays(1).atStartOfDay();
        List<Run> runList = runRepository.findByRegDateGreaterThanEqualAndRegDateLessThan(startDateTime, endDateTimeExclusive);

        Map<String, Long> countByBucketKey = new HashMap<>();
        for (Run run : runList) {
            if (run.getRegDate() == null) {
                continue;
            }
            LocalDate runDate = run.getRegDate().toLocalDate();
            String bucketKey = toBucketKey(runDate, safeBucketType);
            countByBucketKey.put(bucketKey, countByBucketKey.getOrDefault(bucketKey, 0L) + 1L);
        }

        List<UsageStatsPointDto> series = buildSeries(safeBucketType, resolvedStartDate, resolvedEndDate, countByBucketKey);

        return UsageStatsResponseDto.builder()
                .bucketType(safeBucketType)
                .startDate(resolvedStartDate)
                .endDate(resolvedEndDate)
                .totalCount((long) runList.size())
                .series(series)
                .build();
    }

    private String toBucketKey(LocalDate date, UsageStatsBucketType bucketType) {
        return switch (bucketType) {
            case DAY -> date.toString();
            case WEEK -> date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toString();
            case MONTH -> YearMonth.from(date).toString();
            case YEAR -> Integer.toString(date.getYear());
        };
    }

    private List<UsageStatsPointDto> buildSeries(
            UsageStatsBucketType bucketType,
            LocalDate startDate,
            LocalDate endDate,
            Map<String, Long> countByBucketKey
    ) {
        List<UsageStatsPointDto> series = new ArrayList<>();

        switch (bucketType) {
            case DAY -> {
                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                    String key = date.toString();
                    String label = date.getMonthValue() + "/" + date.getDayOfMonth();
                    series.add(UsageStatsPointDto.builder()
                            .label(label)
                            .count(countByBucketKey.getOrDefault(key, 0L))
                            .build());
                }
            }
            case WEEK -> {
                LocalDate weekStart = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate weekEnd = endDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                for (LocalDate date = weekStart; !date.isAfter(weekEnd); date = date.plusWeeks(1)) {
                    String key = date.toString();
                    int weekNo = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                    int weekYear = date.get(IsoFields.WEEK_BASED_YEAR);
                    String label = weekYear + "-" + String.format("%02d", weekNo) + "주";
                    series.add(UsageStatsPointDto.builder()
                            .label(label)
                            .count(countByBucketKey.getOrDefault(key, 0L))
                            .build());
                }
            }
            case MONTH -> {
                YearMonth monthStart = YearMonth.from(startDate);
                YearMonth monthEnd = YearMonth.from(endDate);
                for (YearMonth ym = monthStart; !ym.isAfter(monthEnd); ym = ym.plusMonths(1)) {
                    String key = ym.toString();
                    String label = ym.getYear() + "-" + String.format("%02d", ym.getMonthValue());
                    series.add(UsageStatsPointDto.builder()
                            .label(label)
                            .count(countByBucketKey.getOrDefault(key, 0L))
                            .build());
                }
            }
            case YEAR -> {
                for (int year = startDate.getYear(); year <= endDate.getYear(); year++) {
                    String key = Integer.toString(year);
                    series.add(UsageStatsPointDto.builder()
                            .label(key)
                            .count(countByBucketKey.getOrDefault(key, 0L))
                            .build());
                }
            }
        }

        return series;
    }
}
