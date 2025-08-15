package com.runcombi.server.admin.controller;

import com.runcombi.server.domain.announcement.dto.ResponseAnnouncementDto;
import com.runcombi.server.domain.announcement.entity.Announcement;
import com.runcombi.server.domain.announcement.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AnnouncementService announcementService;

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
}
