package com.runcombi.server.domain.calender.service;

import com.runcombi.server.domain.calender.dto.MonthRunDto;
import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.member.repository.MemberRepository;
import com.runcombi.server.domain.run.repository.RunRepository;
import com.runcombi.server.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.runcombi.server.global.exception.code.CustomErrorList.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalenderService {
    private final RunRepository runRepository;
    private final MemberRepository memberRepository;

    public List<MonthRunDto> getMonthData(Member contextMember, int year, int month) {
        if(month > 12 || month < 1) throw new CustomException(CALENDER_MONTH_ERROR);
        Member member = memberRepository.findByMemberId(contextMember.getMemberId());

        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);

        LocalDateTime startOfMonth = firstDay.atStartOfDay();
        LocalDateTime endOfMonth = lastDay.plusDays(1).atStartOfDay();

        // 전체 데이터 조회
        List<Object[]> results = runRepository.findRunIdsAndDates(member, startOfMonth, endOfMonth);

        // 날짜별 Map 초기화
        Map<LocalDate, List<Long>> dateMap = new LinkedHashMap<>();
        for (LocalDate date = firstDay; !date.isAfter(lastDay); date = date.plusDays(1)) {
            dateMap.put(date, new ArrayList<>());
        }

        // 데이터 맵핑
        for (Object[] row : results) {
            Long runId = (Long) row[0];
            LocalDateTime regDateTime = (LocalDateTime) row[1];
            LocalDate regDate = regDateTime.toLocalDate();

            List<Long> list = dateMap.get(regDate);
            if (list != null) {
                list.add(runId);
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        // 배열로 날자값과 runId 를 담아 반환
        List<MonthRunDto> resultList = new ArrayList<>();
        for (LocalDate date : dateMap.keySet()) {
            String key = date.format(formatter); // "yyyyMMdd"
            List<Long> runIds = dateMap.get(date);

            // runIds가 비어있으면 null 유지
            if (runIds.isEmpty()) {
                runIds = null;
            }
            resultList.add(new MonthRunDto(key, runIds));
        }

        return resultList;
    }
}
