package com.runcombi.server.admin.service;

import com.runcombi.server.admin.dto.UsageStatsBucketType;
import com.runcombi.server.admin.dto.UsageStatsMemberRunsResponseDto;
import com.runcombi.server.admin.dto.UsageStatsMemberSummaryDto;
import com.runcombi.server.admin.dto.UsageStatsMembersResponseDto;
import com.runcombi.server.admin.dto.UsageStatsPointDto;
import com.runcombi.server.admin.dto.UsageStatsResponseDto;
import com.runcombi.server.admin.dto.UsageStatsRunDetailDto;
import com.runcombi.server.admin.dto.UsageStatsRunPetDto;
import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.member.repository.MemberRepository;
import com.runcombi.server.domain.run.entity.Run;
import com.runcombi.server.domain.run.entity.RunPet;
import com.runcombi.server.domain.run.repository.RunRepository;
import com.runcombi.server.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.runcombi.server.global.exception.code.CustomErrorList.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AdminStatisticsService {
    private final RunRepository runRepository;
    private final MemberRepository memberRepository;

    public UsageStatsResponseDto getRunUsageStats(
            UsageStatsBucketType bucketType,
            LocalDate startDate,
            LocalDate endDate
    ) {
        UsageStatsBucketType safeBucketType = bucketType == null ? UsageStatsBucketType.WEEK : bucketType;
        LocalDate[] resolvedRange = resolveStatsRange(safeBucketType, startDate, endDate);
        LocalDate resolvedStartDate = resolvedRange[0];
        LocalDate resolvedEndDate = resolvedRange[1];

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

    public UsageStatsMembersResponseDto getUsageMembers(LocalDate startDate, LocalDate endDate) {
        LocalDate[] resolvedRange = normalizeRange(startDate, endDate);
        LocalDate resolvedStartDate = resolvedRange[0];
        LocalDate resolvedEndDate = resolvedRange[1];

        LocalDateTime startDateTime = resolvedStartDate.atStartOfDay();
        LocalDateTime endDateTimeExclusive = resolvedEndDate.plusDays(1).atStartOfDay();

        List<Run> runList = runRepository.findWithMemberAndPetsByRegDateRange(startDateTime, endDateTimeExclusive);
        Map<Long, MemberUsageAccumulator> memberUsageMap = new HashMap<>();

        for (Run run : runList) {
            Member member = run.getMember();
            if (member == null) {
                continue;
            }

            MemberUsageAccumulator accumulator = memberUsageMap.computeIfAbsent(
                    member.getMemberId(),
                    ignored -> new MemberUsageAccumulator(member)
            );
            accumulator.accept(run);
        }

        List<UsageStatsMemberSummaryDto> members = memberUsageMap.values().stream()
                .map(MemberUsageAccumulator::toDto)
                .sorted(
                        Comparator.comparing(UsageStatsMemberSummaryDto::getRunCount, Comparator.reverseOrder())
                                .thenComparing(UsageStatsMemberSummaryDto::getLastRunDate, Comparator.nullsLast(Comparator.reverseOrder()))
                                .thenComparing(UsageStatsMemberSummaryDto::getMemberId)
                )
                .collect(Collectors.toList());

        return UsageStatsMembersResponseDto.builder()
                .startDate(resolvedStartDate)
                .endDate(resolvedEndDate)
                .totalRunCount((long) runList.size())
                .totalMemberCount(members.size())
                .members(members)
                .build();
    }

    public UsageStatsMemberRunsResponseDto getMemberRuns(Long memberId, LocalDate startDate, LocalDate endDate) {
        Member member = memberRepository.findByMemberId(memberId);
        if (member == null) {
            throw new CustomException(MEMBER_NOT_FOUND);
        }

        LocalDate[] resolvedRange = normalizeRange(startDate, endDate);
        LocalDate resolvedStartDate = resolvedRange[0];
        LocalDate resolvedEndDate = resolvedRange[1];

        LocalDateTime startDateTime = resolvedStartDate.atStartOfDay();
        LocalDateTime endDateTimeExclusive = resolvedEndDate.plusDays(1).atStartOfDay();
        List<Run> runList = runRepository.findWithMemberAndPetsByMemberIdAndRegDateRange(memberId, startDateTime, endDateTimeExclusive);

        List<UsageStatsRunDetailDto> runs = new ArrayList<>();
        for (Run run : runList) {
            List<UsageStatsRunPetDto> pets = run.getRunPets().stream()
                    .map(this::toRunPetDto)
                    .filter(dto -> dto.getPetId() != null)
                    .sorted(Comparator.comparing(UsageStatsRunPetDto::getPetId))
                    .collect(Collectors.toList());

            UsageStatsRunDetailDto runDto = UsageStatsRunDetailDto.builder()
                    .runId(run.getRunId())
                    .regDate(run.getRegDate())
                    .runTime(run.getRunTime())
                    .runDistance(run.getRunDistance())
                    .memberCal(run.getMemberCal())
                    .memberRunStyle(run.getMemberRunStyle() == null ? null : run.getMemberRunStyle().name())
                    .runEvaluating(run.getRunEvaluating() == null ? null : run.getRunEvaluating().name())
                    .memo(run.getMemo())
                    .runImageUrl(run.getRunImageUrl())
                    .routeImageUrl(run.getRouteImageUrl())
                    .pets(pets)
                    .build();

            runs.add(runDto);
        }

        return UsageStatsMemberRunsResponseDto.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .startDate(resolvedStartDate)
                .endDate(resolvedEndDate)
                .totalRunCount((long) runs.size())
                .runs(runs)
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
                            .bucketStartDate(date)
                            .bucketEndDate(date)
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
                    LocalDate bucketStart = maxDate(date, startDate);
                    LocalDate bucketEnd = minDate(date.plusDays(6), endDate);
                    series.add(UsageStatsPointDto.builder()
                            .label(label)
                            .count(countByBucketKey.getOrDefault(key, 0L))
                            .bucketStartDate(bucketStart)
                            .bucketEndDate(bucketEnd)
                            .build());
                }
            }
            case MONTH -> {
                YearMonth monthStart = YearMonth.from(startDate);
                YearMonth monthEnd = YearMonth.from(endDate);
                for (YearMonth ym = monthStart; !ym.isAfter(monthEnd); ym = ym.plusMonths(1)) {
                    String key = ym.toString();
                    String label = ym.getYear() + "-" + String.format("%02d", ym.getMonthValue());
                    LocalDate bucketStart = maxDate(ym.atDay(1), startDate);
                    LocalDate bucketEnd = minDate(ym.atEndOfMonth(), endDate);
                    series.add(UsageStatsPointDto.builder()
                            .label(label)
                            .count(countByBucketKey.getOrDefault(key, 0L))
                            .bucketStartDate(bucketStart)
                            .bucketEndDate(bucketEnd)
                            .build());
                }
            }
            case YEAR -> {
                for (int year = startDate.getYear(); year <= endDate.getYear(); year++) {
                    String key = Integer.toString(year);
                    LocalDate bucketStart = maxDate(LocalDate.of(year, 1, 1), startDate);
                    LocalDate bucketEnd = minDate(LocalDate.of(year, 12, 31), endDate);
                    series.add(UsageStatsPointDto.builder()
                            .label(key)
                            .count(countByBucketKey.getOrDefault(key, 0L))
                            .bucketStartDate(bucketStart)
                            .bucketEndDate(bucketEnd)
                            .build());
                }
            }
        }

        return series;
    }

    private UsageStatsRunPetDto toRunPetDto(RunPet runPet) {
        if (runPet == null || runPet.getPet() == null) {
            return UsageStatsRunPetDto.builder().build();
        }

        return UsageStatsRunPetDto.builder()
                .petId(runPet.getPet().getPetId())
                .petName(runPet.getPet().getName())
                .petCal(runPet.getPetCal())
                .build();
    }

    private LocalDate[] resolveStatsRange(UsageStatsBucketType bucketType, LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        if (startDate != null && endDate != null) {
            return normalizeRange(startDate, endDate);
        }

        LocalDate resolvedEndDate = today;
        LocalDate resolvedStartDate = switch (bucketType) {
            case DAY -> today.minusDays(6);
            case WEEK -> today.minusWeeks(11);
            case MONTH -> today.minusMonths(11);
            case YEAR -> today.minusYears(4);
        };
        return new LocalDate[]{resolvedStartDate, resolvedEndDate};
    }

    private LocalDate[] normalizeRange(LocalDate startDate, LocalDate endDate) {
        LocalDate resolvedStartDate = startDate == null ? LocalDate.now() : startDate;
        LocalDate resolvedEndDate = endDate == null ? resolvedStartDate : endDate;

        if (resolvedStartDate.isAfter(resolvedEndDate)) {
            return new LocalDate[]{resolvedEndDate, resolvedStartDate};
        }
        return new LocalDate[]{resolvedStartDate, resolvedEndDate};
    }

    private LocalDate maxDate(LocalDate first, LocalDate second) {
        return first.isAfter(second) ? first : second;
    }

    private LocalDate minDate(LocalDate first, LocalDate second) {
        return first.isBefore(second) ? first : second;
    }

    private Double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private class MemberUsageAccumulator {
        private final Long memberId;
        private final String email;
        private final String nickname;
        private final String provider;
        private final String profileImgUrl;
        private final String memberStatus;
        private long runCount;
        private int totalRunTime;
        private double totalRunDistance;
        private LocalDateTime lastRunDate;

        private MemberUsageAccumulator(Member member) {
            this.memberId = member.getMemberId();
            this.email = member.getEmail();
            this.nickname = member.getNickname();
            this.provider = member.getProvider() == null ? null : member.getProvider().name();
            this.profileImgUrl = member.getProfileImgUrl();
            this.memberStatus = member.getIsActive() == null ? null : member.getIsActive().name();
        }

        public void accept(Run run) {
            this.runCount++;

            if (run.getRunTime() != null) {
                this.totalRunTime += run.getRunTime();
            }
            if (run.getRunDistance() != null) {
                this.totalRunDistance += run.getRunDistance();
            }
            if (run.getRegDate() != null && (this.lastRunDate == null || run.getRegDate().isAfter(this.lastRunDate))) {
                this.lastRunDate = run.getRegDate();
            }
        }

        public UsageStatsMemberSummaryDto toDto() {
            return UsageStatsMemberSummaryDto.builder()
                    .memberId(memberId)
                    .email(email)
                    .nickname(nickname)
                    .provider(provider)
                    .profileImgUrl(profileImgUrl)
                    .memberStatus(memberStatus)
                    .runCount(runCount)
                    .totalRunTime(totalRunTime)
                    .totalRunDistance(roundToTwoDecimals(totalRunDistance))
                    .lastRunDate(lastRunDate)
                    .build();
        }
    }
}
