package com.runcombi.server.admin.controller;

import com.runcombi.server.domain.announcement.dto.ResponseAnnouncementDto;
import com.runcombi.server.domain.announcement.entity.Announcement;
import com.runcombi.server.domain.announcement.service.AnnouncementService;
import com.runcombi.server.domain.member.entity.Member;
import com.runcombi.server.domain.member.entity.Role;
import com.runcombi.server.domain.member.repository.MemberRepository;
import com.runcombi.server.domain.version.entity.OS;
import com.runcombi.server.domain.version.entity.Version;
import com.runcombi.server.domain.version.service.VersionService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;

@Hidden
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AnnouncementService announcementService;
    private final MemberRepository memberRepository;
    private final VersionService versionService;

    @GetMapping({"", "/"})
    public String adminEntry(Authentication authentication) {
        boolean isAuthenticated = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);

        if (isAuthenticated) {
            return "redirect:/admin/home";
        }
        return "redirect:/admin/login";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        return "admin/login";
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        return "admin/home";
    }

    @GetMapping("/announcement")
    public String announcementPage(Model model) {
        List<Announcement> announcementList = announcementService.getAllAnnouncementList();
        model.addAttribute("announcementList", announcementList);
        return "admin/announcement";
    }

    @GetMapping("/member")
    public String memberPage(Model model) {
        List<Member> memberList = memberRepository.findByRole(Role.USER);
        model.addAttribute("memberList", memberList);
        return "admin/member";
    }

    @GetMapping("/version")
    public String versionPage(Model model) {
        HashMap<String, String> version = versionService.getVersion();
        model.addAttribute("version", version);
        model.addAttribute("iOSVersionList", versionService.getVersionHistory(OS.iOS));
        model.addAttribute("AndroidVersionList", versionService.getVersionHistory(OS.Android));
        return "admin/version";
    }

    @GetMapping("/event")
    public String eventPage(Model model) {
        List<Announcement> eventList = announcementService.getEventList();
        model.addAttribute("eventList", eventList);
        return "admin/event";
    }

    @GetMapping("/updateAnnouncement/{id}")
    public String updateAnnouncementPage(@PathVariable("id") Long announcementId, Model model) {
        HashMap<String, String> announcementDetail = announcementService.getAnnouncementDetail(announcementId);
        model.addAttribute("announcementDetail", announcementDetail);

        return "admin/updateAnnouncement";
    }
}
