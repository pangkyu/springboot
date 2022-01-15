package com.example.chapter6.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
public class MemberController {

    @Autowired
    MemberService memberService;

    @GetMapping("/login")
    public String memberLogin() {
        return "member/login";
    }

    @GetMapping("/join")
    public String mebmerJoin() {
        return "member/join";
    }

    @RequestMapping("/find_id")
    public String findId() {
        return "member/find_id";
    }

    @RequestMapping("/find_pw")
    public String findPw() {
        return "member/find_pw";
    }

    @PostMapping("/join")
    public String memberJoinPost() {
        return "member/join";
    }

}

