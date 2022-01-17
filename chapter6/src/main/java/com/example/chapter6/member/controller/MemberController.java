package com.example.chapter6.member.controller;

import com.example.chapter6.Service.MemberService;
import com.example.chapter6.model.MemberVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.lang.reflect.Member;
import java.util.Map;

@Controller
@RequestMapping("/member")
public class MemberController {
    private Logger logger = LoggerFactory.getLogger(MemberController.class);

    private MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/login")
    public String memberLogin() {
        return "member/login";
    }

    @GetMapping("/join")
    public String mebmerJoin() {
        return "member/join";
    }


    /**
     * 회원 가입 처리
     * @param memberVO
     * @param errors
     * @param model
     * @return
     */
    @PostMapping("/join")
    public String memberJoinPost(
            @Valid MemberVO memberVO, Errors errors, Model model
    ) throws Exception {

        if (errors.hasErrors()) {
            Map<String, String> validate = memberService.formValidation(errors);

            for (String key : validate.keySet()) {
                logger.info(key, validate.get(key));
                model.addAttribute(key, validate.get(key));
            }

            return "member/join";
        }

        boolean idCheck = memberService.duplicateId(memberVO.getUserId());
        boolean emailCheck = memberService.duplicateEmail(memberVO.getEmail());

        if (!idCheck && !emailCheck) memberService.insertMember(memberVO);

        return "redirect:/member/login";
    }


    @RequestMapping("/find_id")
    public String findId() {
        return "member/find_id";
    }

    @RequestMapping("/find_pw")
    public String findPw() {
        return "member/find_pw";
    }


}

