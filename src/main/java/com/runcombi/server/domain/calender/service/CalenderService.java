package com.runcombi.server.domain.calender.service;

import com.runcombi.server.domain.calender.dto.*;
import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.member.repository.MemberRepository;
import com.runcombi.server.domain.pet.entity.Pet;
import com.runcombi.server.domain.pet.repository.PetRepository;
import com.runcombi.server.domain.run.entity.Run;
import com.runcombi.server.domain.run.entity.RunEvaluating;
import com.runcombi.server.domain.run.entity.RunPet;
import com.runcombi.server.domain.run.repository.RunRepository;
import com.runcombi.server.global.exception.CustomException;
import com.runcombi.server.global.s3.dto.S3ImageReturnDto;
import com.runcombi.server.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final PetRepository petRepository;
    private final S3Service s3Service;

    public ResponseMonthRunDto getMonthData(Member contextMember, int year, int month) {
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
        List<RequestMonthRunDto> resultList = new ArrayList<>();
        for (LocalDate date : dateMap.keySet()) {
            String key = date.format(formatter); // "yyyyMMdd"
            List<Long> runIds = dateMap.get(date);

            // runIds가 비어있으면 null 유지
            if (runIds.isEmpty()) {
                runIds = null;
            }
            resultList.add(new RequestMonthRunDto(key, runIds));
        }

        // 한 달 평균 소모 칼로리 계산 ( null / 정수 )
        Integer avgCal = runRepository.findAverageMemberCal(member, startOfMonth, endOfMonth);
        System.out.println("avgCal >>> " + avgCal);

        // 한 달 평균 산책 거리 계산 ( null / 소수점 2자리 )
        Double avgDistance = runRepository.findAverageRunDistance(member, startOfMonth, endOfMonth);
        System.out.println("avgDistance >>> " + avgDistance);

        // 한 달 내 가장 많은 산책 스타일 계산
        String topRunStyle = null;
        List<Object[]> runStyleCounts = runRepository.findRunStyleCounts(member.getMemberId(), startOfMonth, endOfMonth);
        if(!runStyleCounts.isEmpty()) {
            Object[] first = runStyleCounts.getFirst();
            topRunStyle = (String) first[0];
            System.out.println("topRunStyle >>> " + topRunStyle);
        }

        return new ResponseMonthRunDto(resultList, avgCal, avgDistance, topRunStyle);
    }

    public List<ResponseDayRunDto> getDayData(Member member, int year, int month, int day) {
        if(month > 12 || month < 1) throw new CustomException(CALENDER_MONTH_ERROR);

        // 기간 계산
        LocalDate startDay = LocalDate.of(year, month, day);
        LocalDate endDay = LocalDate.of(year, month, day);

        LocalDateTime start = startDay.atStartOfDay();
        LocalDateTime end = endDay.plusDays(1).atStartOfDay();

        List<Run> runs = runRepository.findByMemberAndRegDateBetween(member, start, end);

        List<ResponseDayRunDto> runList = new ArrayList<>();
        for(Run run : runs) {
            runList.add(ResponseDayRunDto.builder()
                            .runId(run.getRunId())
                            .runTime(run.getRunTime())
                            .runDistance(run.getRunDistance())
                            .runImageUrl(run.getRunImageUrl())
                            .regDate(run.getRegDate())
                            .build());
        }

        return runList;
    }

    public ResponseRunDto getRunData(Member contextMember, Long runId) {
        Member member = memberRepository.findByMemberId(contextMember.getMemberId());
        Run run = runRepository.findById(runId).orElseThrow(() -> new CustomException(RUN_ID_INVALID));

        // 해당 회원의 운동 정보가 아닌 경우 예외처리
        if(run.getMember() != member) throw new CustomException(RUN_MEMBER_NOT_MATCH);

        List<ResponseRunPetDto> responseRunPetDtoList = new ArrayList<>();
        List<RunPet> runPets = run.getRunPets();
        for(RunPet runpet : runPets) {
            Pet pet = petRepository.findByPetId(runpet.getPet().getPetId());
            responseRunPetDtoList.add(
                    ResponseRunPetDto.builder()
                            .petId(pet.getPetId())
                            .name(pet.getName())
                            .petImageUrl(pet.getPetImageUrl())
                            .petCal(runpet.getPetCal())
                            .build()
            );
        }

        return ResponseRunDto.builder()
                .runId(run.getRunId())
                .runTime(run.getRunTime())
                .runDistance(run.getRunDistance())
                .memberRunStyle(run.getMemberRunStyle())
                .runEvaluating(run.getRunEvaluating())
                .runImageUrl(run.getRouteImageUrl())
                .routeImageUrl(run.getRouteImageUrl())
                .memo(run.getMemo())
                .regDate(run.getRegDate())
                .petData(responseRunPetDtoList)
                .build();
    }

    @Transactional
    public void setRunEvaluating(Member contextMember, Long runId, RunEvaluating runEvaluating) {
        Member member = memberRepository.findByMemberId(contextMember.getMemberId());
        Run run = runRepository.findById(runId).orElseThrow(() -> new CustomException(RUN_ID_INVALID));

        // 해당 회원의 운동 정보가 아닌 경우 예외처리
        if(run.getMember() != member) throw new CustomException(RUN_MEMBER_NOT_MATCH);

        run.updateRunEvaluating(runEvaluating);
        runRepository.save(run);
    }

    @Transactional
    public void setRunImage(Member contextMember, Long runId, MultipartFile runImage) {
        Member member = memberRepository.findByMemberId(contextMember.getMemberId());
        Run run = runRepository.findById(runId).orElseThrow(() -> new CustomException(RUN_ID_INVALID));

        // 해당 회원의 운동 정보가 아닌 경우 예외처리
        if(run.getMember() != member) throw new CustomException(RUN_MEMBER_NOT_MATCH);

        // 이미지가 있으면 삭제
        if(!run.getRunImageKey().isEmpty()) {
            s3Service.deleteImage(run.getRunImageKey());
        }

        // 이미지 저장
        S3ImageReturnDto s3ImageReturnDto = s3Service.uploadRunImage(runImage, run.getRunId());
        run.setRunImage(s3ImageReturnDto);

        runRepository.save(run);
    }

    @Transactional
    public void updateMemo(Member contextMember, Long runId, String memo) {
        Member member = memberRepository.findByMemberId(contextMember.getMemberId());
        Run run = runRepository.findById(runId).orElseThrow(() -> new CustomException(RUN_ID_INVALID));

        // 해당 회원의 운동 정보가 아닌 경우 예외처리
        if(run.getMember() != member) throw new CustomException(RUN_MEMBER_NOT_MATCH);

        run.updateMemo(memo);
        runRepository.save(run);
    }

    @Transactional
    public void deleteRun(Member contextMember, Long runId) {
        Member member = memberRepository.findByMemberId(contextMember.getMemberId());
        Run run = runRepository.findById(runId).orElseThrow(() -> new CustomException(RUN_ID_INVALID));

        // 해당 회원의 운동 정보가 아닌 경우 예외처리
        if(run.getMember() != member) throw new CustomException(RUN_MEMBER_NOT_MATCH);

        runRepository.delete(run);
    }
}
