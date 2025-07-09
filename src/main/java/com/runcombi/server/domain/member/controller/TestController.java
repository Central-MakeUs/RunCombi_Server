package com.runcombi.server.domain.member.controller;

import com.runcombi.server.domain.member.repository.MemberRepository;
import com.runcombi.server.global.exception.CustomException;
import com.runcombi.server.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.runcombi.server.global.exception.code.CustomErrorList.MEMBER_NOT_FOUND;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private final MemberRepository memberRepository;
    @GetMapping("/fail")
    public void fail() {
        throw new CustomException(MEMBER_NOT_FOUND);
    }

    @GetMapping("/success")
    public ApiResponse<String> success() {
        return ApiResponse.onSuccess("성공입니다.");
    }

    @GetMapping("/success-list")
    public ApiResponse<List<String>> successList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("첫번째");
        list.add("두번째");
        return ApiResponse.onSuccess(list);
    }
}
