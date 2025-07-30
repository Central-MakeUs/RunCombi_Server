package com.runcombi.server.domain.calender.service;

import com.runcombi.server.domain.calender.dto.*;
import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.member.repository.MemberRepository;
import com.runcombi.server.domain.pet.entity.Pet;
import com.runcombi.server.domain.pet.repository.PetRepository;
import com.runcombi.server.domain.run.dto.PetCalDto;
import com.runcombi.server.domain.run.entity.Run;
import com.runcombi.server.domain.run.entity.RunEvaluating;
import com.runcombi.server.domain.run.entity.RunPet;
import com.runcombi.server.domain.run.repository.RunPetRepository;
import com.runcombi.server.domain.run.repository.RunRepository;
import com.runcombi.server.domain.run.service.RunService;
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
import java.util.stream.Collectors;

import static com.runcombi.server.global.exception.code.CustomErrorList.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalenderService {
    private final RunRepository runRepository;
    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final RunPetRepository runPetRepository;
    private final RunService runService;
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

        // 한 달 평균 산책 시간 계산 ( null / 정수 )
        Integer avgTime = runRepository.findAverageMemberRunTime(member, startOfMonth, endOfMonth);

        // 한 달 평균 소모 칼로리 계산 ( null / 정수 )
        Integer avgCal = runRepository.findAverageMemberCal(member, startOfMonth, endOfMonth);

        // 한 달 평균 산책 거리 계산 ( null / 소수점 2자리 )
        Double avgDistance = runRepository.findAverageRunDistance(member, startOfMonth, endOfMonth);

        // 한 달 내 가장 많은 산책 스타일 계산
        String topRunStyle = null;
        List<Object[]> runStyleCounts = runRepository.findRunStyleCounts(member.getMemberId(), startOfMonth, endOfMonth);
        if(!runStyleCounts.isEmpty()) {
            Object[] first = runStyleCounts.getFirst();
            topRunStyle = (String) first[0];
        }

        return new ResponseMonthRunDto(resultList, avgTime, avgCal, avgDistance, topRunStyle);
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
                .nickname(member.getNickname())
                .profileImgUrl(member.getProfileImgUrl())
                .runTime(run.getRunTime())
                .runDistance(run.getRunDistance())
                .memberRunStyle(run.getMemberRunStyle())
                .memberCal(run.getMemberCal())
                .runEvaluating(run.getRunEvaluating())
                .runImageUrl(run.getRunImageUrl())
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

        // 이미지 확장자 검증
        if(!runImage.isEmpty()) s3Service.validateImageFile(runImage);

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

    @Transactional
    public void addRun(Member contextMember, RequestAddRunDto addRunData) {
        Member member = memberRepository.findByMemberId(contextMember.getMemberId());
        List<Pet> pets = member.getPets();

        // Pet 유효성 검사 - Start
        // 넘겨받은 petId 가 현재 가진 펫 정보와 일치하는지 검사
        Set<Long> petIdSet = pets.stream()
                .map(Pet::getPetId)
                .collect(Collectors.toSet());

        List<PetCalDto> petCalList = addRunData.getPetCalList();

        for (PetCalDto petCalData : petCalList) {
            // 회원의 펫이 아닌 경우 예외 처리
            if (!petIdSet.contains(petCalData.getPetId())) {
                throw new CustomException(PET_NOT_MATCH);
            }
        }

        // run 데이터 생성
        Run run = runRepository.save(Run.builder()
                .member(member)
                .memberRunStyle(addRunData.getMemberRunStyle())
                .memberCal(runService.getMemberCal(member.getGender(), addRunData.getMemberRunStyle(), member.getWeight(), addRunData.getRunTime()))
                .runTime(addRunData.getRunTime())
                .runDistance(addRunData.getRunDistance())
                .build());

        for(PetCalDto petCalData : petCalList) {
            Pet pet= petRepository.findByPetId(petCalData.getPetId());
            RunPet runPet = runPetRepository.save(RunPet.builder()
                    .pet(petRepository.findByPetId(petCalData.getPetId()))
                    .petCal(runService.getPetCal(pet.getRunStyle(), pet.getWeight(), addRunData.getRunTime()))
                    .build());

            run.setRunPets(runPet);
        }

        run.setRegDate(addRunData.getRegDate());
    }

    @Transactional
    public void updateRunDetail(Member contextMember, RequestUpdateRunDetailDto requestUpdateRunDetailDto) {
        Member member = memberRepository.findByMemberId(contextMember.getMemberId());
        Run run = runRepository.findById(requestUpdateRunDetailDto.getRunId()).orElseThrow(() -> new CustomException(RUN_ID_INVALID));

        // 해당 회원의 운동 정보가 아닌 경우 예외처리
        if(run.getMember() != member) throw new CustomException(RUN_MEMBER_NOT_MATCH);

        // 세부 내용 업데이트
        run.updateRunDetail(requestUpdateRunDetailDto, runService.getMemberCal(member.getGender(), requestUpdateRunDetailDto.getMemberRunStyle(), member.getWeight(), requestUpdateRunDetailDto.getRunTime()));

        // 반려 동물 칼로리 업데이트
        List<RunPet> runPets = run.getRunPets();
        for(RunPet runPet : runPets) {
            Pet pet = runPet.getPet();
            runPet.updateCal(runService.getPetCal(pet.getRunStyle(), pet.getWeight(), requestUpdateRunDetailDto.getRunTime()));
        }
    }
}
