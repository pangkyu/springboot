package com.example.chapter6.member.controller;

import com.example.chapter6.Util.Util;
import com.example.chapter6.member.service.MemberService;
import com.example.chapter6.model.MemberVO;
import com.example.chapter6.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/member")
public class MemberController {

    private Logger logger = LoggerFactory.getLogger(MemberController.class);

    private MemberService memberService;

    public MemberController(MemberService memberService){
        this.memberService = memberService;
    }

//    로그인 페이지
    @GetMapping("/login")
    public String memberLogin(){
        return "member/login";
    }

//    회원가입 페이지
    @GetMapping("/join")
    public String mebmerJoin(){
        return "member/join";
    }


//    회원가입 처리
    @PostMapping("/join")
    public String memberJoinPost(
            @Valid MemberVO memberVO, Errors errors, Model model
    ) throws Exception {

        if(errors.hasErrors()){
            Map<String, String> validate = memberService.formValidation(errors);

            for(String key : validate.keySet()){
                logger.info(key, validate.get(key));
                model.addAttribute(key, validate.get(key));
            }

            return "member/join";
        }

        boolean idCheck = memberService.duplicateId(memberVO.getUserId());
        boolean emailCheck = memberService.duplicateEmail(memberVO.getEmail());

        if(!idCheck && !emailCheck) memberService.insertMember(memberVO);

        return "redirect:/member/login";
    }

//    로그인 처리
    @PostMapping("/loginProcess")
    public String loginProcess(
            @RequestParam(value = "userId", defaultValue = "") String userId,
            @RequestParam(value = "password", defaultValue = "") String password,
            HttpServletRequest request
    ) throws Exception{
        if(!userId.equals("") && !password.equals("")){
            MemberVO memberVO = new MemberVO();
            memberVO.setUserId(userId);
            memberVO.setPassword(password);

            Boolean result = memberService.loginProcess(memberVO, request);

            if(result == false){
                return "redirect:/member/login";
            }

            return "redirect:/board/list";
        }

        return "redirect:/member/login";
    }

//    로그아웃
    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(session != null)
            session.invalidate();

        return "redirect:/member/login";
    }

//    아이디 찾기 페이지
    @GetMapping("/find_id")
    public String findId(){
        return "member/find_id";
    }

//    아이디 찾기 처리
    @PostMapping("/find_id")
    public ModelAndView findIdPost(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "email", defaultValue = "") String email,
            ModelAndView mav
    ) throws Exception{
        if(!name.equals("") && !email.equals("")){
            MemberVO memberVO = new MemberVO();
            memberVO.setName(name);
            memberVO.setEmail(email);

            String id = memberService.findUserId(memberVO);

            logger.info("찾은 id - {}", id);

            if(id == null){
                // 찾는 id가 없습니다.
                mav.addObject("data", new Message("찾으시는 계정이 없습니다.", "/member/find_id"));
                mav.setViewName("message/message");
                return mav;
            }else{
                // 찾는 id가 있습니다.
                int idLength = id.length();
                id = id.substring(0, idLength-2);
                id += "**";
                logger.info("id 마스킹 - {}", id);
                mav.addObject("data", new Message(name + "님이 찾으시는 ID는 " + id + "입니다.", "/member/login"));
                mav.setViewName("message/message");
                return mav;
            }
        }

        mav.addObject("data", new Message("이름과 이메일을 확인하세요.", "/member/find_id"));
        mav.setViewName("message/message");
        return mav;
    }


//    비밀번호 찾기 페이지
    @GetMapping("/find_pw")
    public String findPw(){
        return "member/find_pw";
    }

//    비밀번호 찾아서 바꾸기
    @PostMapping("/find_pw")
    public ModelAndView findPwPost(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "email", defaultValue = "") String email,
            @RequestParam(value = "userId", defaultValue = "") String userId,
            ModelAndView mav
    ) throws Exception{
        if(!name.equals("") && !email.equals("") && !userId.equals("")){
            MemberVO memberVO = new MemberVO();
            memberVO.setName(name);
            memberVO.setEmail(email);
            memberVO.setUserId(userId);

            String id = memberService.findPassword(memberVO);

            if(id == null){
                //계정 없음
                mav.addObject("data", new Message("찾으시는 계정이 없습니다.", "/member/find_pw"));
                mav.setViewName("message/message");
                return mav;
            }else{
                // 계정이 있으면 비번을 임의로 변경하고 고지한다. 원래는 이메일로 발송
                String pw = Util.generateRandomString(10);
                logger.info("pw - {}", pw);
                memberVO.setPassword(pw);
                memberService.updatePassword(memberVO);
                mav.addObject("data", new Message("비밀번호가 변경되었습니다.{변경된 비밀번호 : " + pw +"}", "/member/find_pw"));
                mav.setViewName("message/message");
                return mav;
            }
        }

        mav.addObject("data", new Message("입력 정보를 확인하세요.", "/member/find_pw"));
        mav.setViewName("message/message");
        return mav;
    }
}

