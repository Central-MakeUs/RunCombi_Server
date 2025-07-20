package com.runcombi.server.domain.member.service;

import com.runcombi.server.domain.member.dto.GetMemberDetailDto;
import com.runcombi.server.domain.member.dto.MemberDto;
import com.runcombi.server.domain.member.dto.SetMemberDetailDto;
import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.member.entity.MemberStatus;
import com.runcombi.server.domain.member.entity.MemberTerm;
import com.runcombi.server.domain.member.entity.TermType;
import com.runcombi.server.domain.member.repository.MemberRepository;
import com.runcombi.server.domain.member.repository.MemberTermRepository;
import com.runcombi.server.domain.pet.dto.PetDto;
import com.runcombi.server.domain.pet.dto.SetPetDetailDto;
import com.runcombi.server.domain.pet.entity.Pet;
import com.runcombi.server.domain.pet.repository.PetRepository;
import com.runcombi.server.domain.pet.service.PetService;
import com.runcombi.server.global.exception.CustomException;
import com.runcombi.server.global.s3.dto.S3ImageReturnDto;
import com.runcombi.server.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
                petService.deletePet(pet, member);
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
}
