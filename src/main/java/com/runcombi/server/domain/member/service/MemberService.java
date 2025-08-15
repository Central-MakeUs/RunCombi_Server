package com.runcombi.server.domain.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.runcombi.server.auth.apple.service.AppleLoginService;
import com.runcombi.server.auth.jwt.JwtService;
import com.runcombi.server.auth.jwt.dto.ResponseTokenDto;
import com.runcombi.server.auth.kakao.service.KakaoLoginService;
import com.runcombi.server.domain.member.dto.*;
import com.runcombi.server.domain.member.entity.*;
import com.runcombi.server.domain.member.repository.MemberRepository;
import com.runcombi.server.domain.member.repository.MemberTermRepository;
import com.runcombi.server.domain.pet.dto.PetDto;
import com.runcombi.server.domain.pet.dto.SetPetDetailDto;
import com.runcombi.server.domain.pet.entity.Pet;
import com.runcombi.server.domain.pet.repository.PetRepository;
import com.runcombi.server.domain.pet.service.PetService;
import com.runcombi.server.domain.run.entity.Run;
import com.runcombi.server.domain.run.repository.RunRepository;
import com.runcombi.server.global.exception.CustomException;
import com.runcombi.server.global.s3.dto.S3ImageReturnDto;
import com.runcombi.server.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.runcombi.server.global.exception.code.CustomErrorList.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberTermRepository memberTermRepository;
    private final PetService petService;
    private final PetRepository petRepository;
    private final S3Service s3Service;
    private final RunRepository runRepository;
    private final JwtService jwtService;
    private final AppleLoginService appleLoginService;
    private final KakaoLoginService kakaoLoginService;

    @Value("${spring.discord.suggestion-webhook}")
    private String sggWebhookUrl;
    @Value("${spring.discord.leave-webhook}")
    private String leaveWebhookUrl;
    @Value("${spring.discord.app-icon}")
    private String appIconUrl;

    @Transactional
    public void setMemberTerms(List<TermType> agreeTermsList, Member contextMember) {
        Member member = memberRepository.findByMemberId(contextMember.getMemberId());
        List<MemberTerm> originMemberTerms = member.getMemberTerms();

        for(TermType newTermType: agreeTermsList) {
            // 기존에 중복된 동의항목이 있다면 true, 없다면 false
            boolean isDuplicate = originMemberTerms.stream()
                    .anyMatch(memberTerm -> memberTerm.getTermType() == newTermType);

            // 기존 동의항목과 일치하지 않는 새로운 동의항목이라면
            if(!isDuplicate) {
                MemberTerm newMemberTerm = MemberTerm
                        .builder()
                        .member(member)
                        .termType(newTermType)
                        .build();
                member.addMemberTerm(newMemberTerm);
            }
        }

        // 필수 동의 여부
        boolean defaultTermsCheck = checkDefaultTerms(member);

        // 필수 동의 3개를 모두 채우고 멤버의 MemberStatus 가 PENDING_AGREE 상태라면 PENDING_MEMBER_DETAIL 상태로 변경
        if(defaultTermsCheck && member.getIsActive() == MemberStatus.PENDING_AGREE) {
            member.updateIsActive(MemberStatus.PENDING_MEMBER_DETAIL);
        }
    }

    public boolean checkDefaultTerms(Member member) {
        List<TermType> defaultTerms = List.of(
                TermType.TERMS_OF_SERVICE,
                TermType.PRIVACY_POLICY,
                TermType.LOCATION_SERVICE_AGREEMENT
        );

        // 멤버가 동의한 약관의 TermType 집합으로 변환
        Set<TermType> agreedTermTypes = member.getMemberTerms().stream()
                .map(MemberTerm::getTermType)
                .collect(Collectors.toSet());

        // 기본 약관 모두 동의했는지 검사
        return agreedTermTypes.containsAll(defaultTerms);
    }

    public void setMemberDetail(Member member, SetMemberDetailDto memberDetailDto) {
        member.setMemberDetail(memberDetailDto);
    }

    public void setMemberImage(Member member, S3ImageReturnDto memberImageReturnDto) {
        member.setMemberImage(memberImageReturnDto);
    }

    public void memberDetailNullCheck(SetMemberDetailDto memberDetailDto) {
        if(
                memberDetailDto == null ||
                        memberDetailDto.getNickname() == null ||
                        memberDetailDto.getGender() == null ||
                        memberDetailDto.getHeight() == null ||
                        memberDetailDto.getWeight() == null
        ) {
            throw new CustomException(DEFAULT_FIELD_NULL);
        }
    }

    @Transactional
    public void setMemberPetDetail(Member contextMember, SetMemberDetailDto memberDetail, MultipartFile memberImage, SetPetDetailDto petDetail, MultipartFile petImage) {
        Member member = memberRepository.findByMemberId(contextMember.getMemberId());
        // 에러 방지코드 : 만약 사용자가 정보입력 시 오류를 발생시켰다면 이미 펫이 영속성 등록을 통해 DB에 입력된 상황
        //   > 기존 펫을 제거하고 로직 실행
        deletePetByMember(member);

        // 넘겨받은 데이터 유효성 검증
        memberDetailNullCheck(memberDetail);
        petService.petDetailNullCheck(petDetail);

        // 이미지 파일 확장자 검증
        if(memberImage != null) s3Service.validateImageFile(memberImage);
        if(petImage != null) s3Service.validateImageFile(petImage);

        // 회원 정보 저장
        setMemberDetail(member, memberDetail);
        if(memberImage != null) {
            // 회원 이미지 저장
            S3ImageReturnDto memberImageReturnDto = s3Service.uploadMemberImage(memberImage, member.getMemberId());
            setMemberImage(member, memberImageReturnDto);
        }

        // 첫번째 펫 정보 저장
        Pet firstPet = petService.setPetDetail(member, petDetail);
        petRepository.save(firstPet);

        if(petImage != null) {
            // 첫번째 펫 이미지 저장
            S3ImageReturnDto firstPetImageReturnDto = s3Service.uploadPetImage(petImage, firstPet.getPetId());
            petService.setPetImage(firstPet, firstPetImageReturnDto);
        }

        // 회원 상태를 LIVE 로 변경
        member.updateIsActive(MemberStatus.LIVE);
    }

    @Transactional
    public void deletePetByMember(Member member) {
        List<Pet> pets = petRepository.findAllByMember(member);
        if(!pets.isEmpty()) {
            for(Pet pet : pets) {
                petService.deletePetByMember(pet, member);
            }
        }
    }

    public GetMemberDetailDto getMemberPetDetail(Member member, List<Pet> petList) {
        MemberDto memberDto = MemberDto.builder()
                .memberId(member.getMemberId())
                .provider(member.getProvider())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .gender(member.getGender())
                .height(member.getHeight())
                .weight(member.getWeight())
                .isActive(member.getIsActive())
                .profileImgUrl(member.getProfileImgUrl())
                .profileImgKey(member.getProfileImgKey())
                .memberTerms(getMemberTerms(member))
                .build();

        List<PetDto> petListDto = new ArrayList<>();
        for(Pet pet : petList) {
            petListDto.add(
                    PetDto.builder()
                            .petId(pet.getPetId())
                            .name(pet.getName())
                            .age(pet.getAge())
                            .weight(pet.getWeight())
                            .runStyle(pet.getRunStyle())
                            .petImageUrl(pet.getPetImageUrl())
                            .petImageKey(pet.getPetImageKey())
                            .build()
            );
        }

        return GetMemberDetailDto.builder()
                .member(memberDto)
                .petList(petListDto)
                .memberStatus(member.getIsActive())
                .build();
    }

    private List<TermType> getMemberTerms(Member member) {
        List<TermType> returnTermList = new ArrayList<>();
        List<MemberTerm> termList = memberTermRepository.findByMember(member);
        for(MemberTerm term : termList) {
            returnTermList.add(term.getTermType());
        }
        return returnTermList;
    }

    @Transactional
    public void updateMemberDetail(Member member, SetMemberDetailDto updateMemberDto, MultipartFile memberImage) {
        // 파일 확장자 검증
        if(memberImage != null) s3Service.validateImageFile(memberImage);

        member.setMemberDetail(updateMemberDto);

        if(memberImage != null) {
            if(member.getProfileImgKey() != null) {
                s3Service.deleteImage(member.getProfileImgKey());
            }
            S3ImageReturnDto memberImageReturnDto = s3Service.uploadMemberImage(memberImage, member.getMemberId());
            member.setMemberImage(memberImageReturnDto);
        }

        memberRepository.save(member);
    }

    @Transactional
    public void deleteAccount(Member contextMember) {
        Member member = memberRepository.findByMemberId(contextMember.getMemberId());

        boolean result = false;
        if(member.getProvider() == Provider.KAKAO) {
            result = kakaoLoginService.unlinkKakaoAccount(member.getKakaoUserId());
            if(!result) throw new CustomException(KAKAO_UNLINK_FAIL);
        } else if(member.getProvider() == Provider.APPLE) {
            result = appleLoginService.revokeAppleToken(member.getAppleRefreshToken());
            if(!result) throw new CustomException(APPLE_REVOKE_FAIL);
        }

        // 산책 이미지 삭제
        List<Run> runList = runRepository.findByMember(member);
        for(Run run : runList) {
            if(run.getRouteImageKey() != null) s3Service.deleteImage(run.getRouteImageKey());
        }

        // 반려 동물 이미지 삭제
        List<Pet> petList = petRepository.findAllByMember(member);
        for(Pet pet : petList) {
            if(pet.getPetImageKey() != null) s3Service.deleteImage(pet.getPetImageKey());
        }

        // 회원 이미지 삭제
        if(member.getProfileImgKey() != null) s3Service.deleteImage(member.getProfileImgKey());

        runRepository.deleteByMember(member);
        memberRepository.delete(member);
    }

    @Transactional
    public void deleteAccount(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId);

        boolean result = false;
        if(member.getProvider() == Provider.KAKAO) {
            result = kakaoLoginService.unlinkKakaoAccount(member.getKakaoUserId());
            if(!result) throw new CustomException(KAKAO_UNLINK_FAIL);
        } else if(member.getProvider() == Provider.APPLE) {
            result = appleLoginService.revokeAppleToken(member.getAppleRefreshToken());
            if(!result) throw new CustomException(APPLE_REVOKE_FAIL);
        }

        // 산책 이미지 삭제
        List<Run> runList = runRepository.findByMember(member);
        for(Run run : runList) {
            if(run.getRouteImageKey() != null) s3Service.deleteImage(run.getRouteImageKey());
        }

        // 반려 동물 이미지 삭제
        List<Pet> petList = petRepository.findAllByMember(member);
        for(Pet pet : petList) {
            if(pet.getPetImageKey() != null) s3Service.deleteImage(pet.getPetImageKey());
        }

        // 회원 이미지 삭제
        if(member.getProfileImgKey() != null) s3Service.deleteImage(member.getProfileImgKey());

        runRepository.deleteByMember(member);
        memberRepository.delete(member);
    }

    @Transactional
    public ResponseTokenDto regenerateAccessToken(String refreshToken) {
        // 토큰 유효성 검사
        if (!jwtService.validateTokenBoolean(refreshToken)) throw new CustomException(REFRESH_TOKEN_INVALID);

        Member member = memberRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        String newRefreshToken = jwtService.createRefreshToken(member.getMemberId(), member.getRole());
        String newAccessToken = jwtService.createAccessToken(member.getMemberId(), member.getRole());
        member.updateRefreshToken(newRefreshToken);
        memberRepository.save(member);

        return ResponseTokenDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    public ResponseDeleteDataDto getDeleteData(Member contextMember) {
        Member member = memberRepository.findByMemberId(contextMember.getMemberId());
        List<Run> runs = runRepository.findByMember(member);
        List<Pet> pets = member.getPets();

        List<String> resultPetName = new ArrayList<>();
        int resultRun = 0;
        int resultRunImage = 0;

        if(pets != null) {
            for(Pet pet : pets) {
                resultPetName.add(pet.getName());
            }
        }

        if(runs != null) {
            for(Run run : runs) {
                resultRun ++;
                if(run.getRunImageKey() != null) resultRunImage ++;
            }
        }

        return ResponseDeleteDataDto.builder()
                .resultPetName(resultPetName)
                .resultRun(resultRun)
                .resultRunImage(resultRunImage)
                .build();
    }

    public void suggestion(Member contextMember, String sggMsg){
        Member member = memberRepository.findByMemberId(contextMember.getMemberId());

        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // JSON 데이터 만들기
            Map<String, Object> embed = new HashMap<>();
            embed.put("title", "새로운 개선제안이 도착했습니다.");
            String totalMsg = String.format(
                    "**회원 정보 및 개선 제안**\n" +
                    "1. 회원 번호 : %s\n" +
                    "2. 이메일 : %s\n" +
                    "3. 가입 SNS : %s\n" +
                    "4. 가입일 : %s\n" +
                    "5. 개선 제안 : %s\n" +
                    "6. 등록 시간 : %s",
                    member.getMemberId(),
                    member.getEmail(),
                    member.getProvider(),
                    member.getRegDate(),
                    sggMsg,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
            embed.put("description", totalMsg);
            Map<String, String> thumbnail = new HashMap<>();
            thumbnail.put("url", appIconUrl);
            embed.put("thumbnail", thumbnail);

            Map<String, Object> jsonData = new HashMap<>();
            jsonData.put("embeds", Collections.singletonList(embed));

            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(jsonData);

            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

            // discord 웹훅 호출
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    sggWebhookUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
        }catch (JsonProcessingException e) {
            throw new CustomException(WEBHOOK_SUGGESTION_ERROR);
        }
    }

    public void leaveReason(Member contextMember, List<String> reason) {
        Member member = memberRepository.findByMemberId(contextMember.getMemberId());

        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // JSON 데이터 만들기
            Map<String, Object> embed = new HashMap<>();
            embed.put("title", "회원 탈퇴 사유가 도착했습니다.");
            String totalMsg = String.format(
                    "**회원 탈퇴 사유**\n" +
                    "1. 회원 번호 : %s\n" +
                    "2. 이메일 : %s\n" +
                    "3. 가입 SNS : %s\n" +
                    "4. 가입일 : %s\n" +
                    "5. 탈퇴 사유 : %s\n" +
                    "6. 탈퇴 시간 : %s",
                    member.getMemberId(),
                    member.getEmail(),
                    member.getProvider(),
                    member.getRegDate(),
                    reason,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
            embed.put("description", totalMsg);
            Map<String, String> thumbnail = new HashMap<>();
            thumbnail.put("url", appIconUrl);
            embed.put("thumbnail", thumbnail);

            Map<String, Object> jsonData = new HashMap<>();
            jsonData.put("embeds", Collections.singletonList(embed));

            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(jsonData);

            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

            // discord 웹훅 호출
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    leaveWebhookUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
        }catch (JsonProcessingException e) {
            throw new CustomException(WEBHOOK_LEAVE_ERROR);
        }
    }
}
