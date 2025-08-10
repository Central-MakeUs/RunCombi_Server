package com.runcombi.server.domain.announcement.service;

import com.runcombi.server.domain.announcement.dto.RequestAddAnnouncementDto;
import com.runcombi.server.domain.announcement.dto.ResponseAnnouncementDetailDto;
import com.runcombi.server.domain.announcement.dto.ResponseAnnouncementDto;
import com.runcombi.server.domain.announcement.entity.*;
import com.runcombi.server.domain.announcement.repository.AnnouncementDetailRepository;
import com.runcombi.server.domain.announcement.repository.AnnouncementRepository;
import com.runcombi.server.domain.announcement.repository.AnnouncementViewRepository;
import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.member.repository.MemberRepository;
import com.runcombi.server.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.runcombi.server.global.exception.code.CustomErrorList.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementService {
    private final MemberRepository memberRepository;
    private final AnnouncementRepository announcementRepository;
    private final AnnouncementDetailRepository announcementDetailRepository;
    private final AnnouncementViewRepository announcementViewRepository;
    @Transactional
    public void addAnnouncement(RequestAddAnnouncementDto request) {
        Announcement announcement = Announcement.builder()
                .announcementType(request.getAnnouncementType())
                .title(request.getTitle())
                .display(request.getDisplay())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .announcementDetail(announcementDetailRepository.save(
                        AnnouncementDetail.builder()
                                .content(request.getContent())
                                .announcementImageUrl(request.getAnnouncementImageUrl())
                                .eventApplyUrl(request.getEventApplyUrl())
                                .build()
                ))
                .build();

        announcementRepository.save(announcement);
    }

    @Transactional
    public void deleteAnnouncement(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId).orElseThrow(() -> new CustomException(ANNOUNCEMENT_NOT_FOUND));

        announcementRepository.delete(announcement);
    }

    public List<ResponseAnnouncementDto> getAnnouncementList(Member contextMember) {
        Member member = memberRepository.findByMemberId(contextMember.getMemberId());

        List<Announcement> findActiveAnnouncementList = announcementRepository.findActiveAnnouncementList(LocalDate.now());
        List<ResponseAnnouncementDto> announcementList = new ArrayList<>();
        for(Announcement announcement : findActiveAnnouncementList) {
            Optional<AnnouncementView> optionalAnnouncementView = announcementViewRepository.findByAnnouncementAndMember(announcement, member);
            String isRead = optionalAnnouncementView
                    .map(view -> "Y")
                    .orElse("N");

            announcementList.add(
                    ResponseAnnouncementDto.builder()
                            .announcementId(announcement.getAnnouncementId())
                            .announcementType(announcement.getAnnouncementType())
                            .title(announcement.getTitle())
                            .startDate(announcement.getStartDate())
                            .endDate(announcement.getEndDate())
                            .regDate(announcement.getRegDate().toLocalDate())
                            .isRead(isRead)
                            .build()
            );
        }

        return announcementList;
    }

    @Transactional
    public ResponseAnnouncementDetailDto getAnnouncementDetail(Member contextMember, Long announcementId) {
        // TODO : 아래 코드처럼 announcementView 생성해서 양방향 매핑하기
        //        AnnouncementView announcementView = AnnouncementView.builder().build();
        //        announcement.addAnnouncementView(announcementView);
        Member member = memberRepository.findByMemberId(contextMember.getMemberId());
        Announcement announcement = announcementRepository.findById(announcementId).orElseThrow(() -> new CustomException(ANNOUNCEMENT_NOT_FOUND));
        AnnouncementDetail announcementDetail = announcement.getAnnouncementDetail();

        Optional<AnnouncementView> optionalAnnouncementView = announcementViewRepository.findByAnnouncementAndMember(announcement, member);
        if(optionalAnnouncementView.isPresent()) {
            // 이미 본 공지사항 및 이벤트인 경우
            return ResponseAnnouncementDetailDto.builder()
                    .announcementId(announcement.getAnnouncementId())
                    .announcementType(announcement.getAnnouncementType())
                    .title(announcement.getTitle())
                    .content(announcementDetail.getContent())
                    .announcementImageUrl(announcementDetail.getAnnouncementImageUrl())
                    .code(optionalAnnouncementView.get().getCode())
                    .eventApplyUrl(announcementDetail.getEventApplyUrl())
                    .startDate(announcement.getStartDate())
                    .endDate(announcement.getEndDate())
                    .regDate(announcement.getRegDate().toLocalDate())
                    .build();
        } else {
            // 처음 본 공지사항 및 이벤트인 경우
            AnnouncementView announcementView = AnnouncementView.builder().build();
//            AnnouncementView announcementView = announcementViewRepository.save(
//                    AnnouncementView.builder()
//                            .code(makeCode(member.getMemberId(), announcement.getTitle()))
//                            .build()
//            );
            // 이벤트의 경우 이벤트 코드 값 추가
            if(announcement.getAnnouncementType() == AnnouncementType.EVENT) {

                String code = makeCode(member.getMemberId(), announcement.getTitle());
                announcementView.setCode(code);
            }
            announcementViewRepository.save(announcementView);

            announcement.addAnnouncementView(announcementView);
            member.addAnnouncementView(announcementView);

            return ResponseAnnouncementDetailDto.builder()
                    .announcementId(announcement.getAnnouncementId())
                    .announcementType(announcement.getAnnouncementType())
                    .title(announcement.getTitle())
                    .content(announcementDetail.getContent())
                    .announcementImageUrl(announcementDetail.getAnnouncementImageUrl())
                    .code(announcementView.getCode())
                    .eventApplyUrl(announcementDetail.getEventApplyUrl())
                    .startDate(announcement.getStartDate())
                    .endDate(announcement.getEndDate())
                    .regDate(announcement.getRegDate().toLocalDate())
                    .build();
        }
    }

    public String makeCode(Long memberId, String announcementTitle) {
        // SHA256 을 사용하여 복호화 불가능한 코드를 생성
        try{
            String text = memberId + announcementTitle;

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes());

            StringBuilder builder = new StringBuilder();
            for (byte b : md.digest()) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch(NoSuchAlgorithmException e) {
            throw new CustomException(ANNOUNCEMENT_MAKE_CODE_ERROR);
        }
    }
}
