package com.example.chapter6.member.controller;

import com.example.chapter6.model.MemberVO;
import com.example.chapter6.model.Message;
import com.example.chapter6.service.MemberService;
import com.example.chapter6.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Controller
@RequestMapping("/member")
public class MemberController {

    private Logger logger = LoggerFactory.getLogger(MemberController.class);

    //@Autowired
    MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/login")
    public String memberLogin() {
        return "member/login";
    }

    @GetMapping("/join")
    public String memberJoin() {
        return "member/join";
    }

    /**
     * 회원가입 처리
     * @param memberVO
     * @param errors
     * @param model
     * @return
     */
    @PostMapping("/join")
    public String memberJoinPost(
            //@RequestParam(value = "userId", defaultValue="") String userId
            @Valid MemberVO memberVO, Errors errors, Model model
    ) throws Exception {

        // 아이디 가입 여부 체크
//        if (!userId.equals("")) {
//            Boolean result = memberService.duplicateId(userId);
//            System.out.println("가입여부 = " + result);
//            logger.info("가입여부 = {}", result);
//            // logger.debug(); // 디버그 정보
//            // logger.info(); // 일반 정보
//            // logger.warn(); // 에러는 아니지만 오류 가능성 있는 것들
//            // logger.error(); // 에러
//        }

        if (errors.hasErrors()) {
            // 검증된 값들이 해시맵에 실려서 리턴돼서 저장됨
            Map<String, String> validate = memberService.formValidation(errors);

            // 모델에 실어서 join.html에 다시 전달
            for (String key : validate.keySet()) {
                logger.info(key, validate.get(key)); // log가 콘솔에 찍힘
                model.addAttribute(key, validate.get(key)); // html 화면에 문구 보이게
            }

            return "member/join";
        }

        boolean idCheck = memberService.duplicateId(memberVO.getUserId());
        boolean emailCheck = memberService.duplicateEmail(memberVO.getEmail());

        if(!idCheck && !emailCheck) {
            memberService.insertMember((memberVO));
        }

        return "redirect:/member/login"; // redirect : 특정 페이지로 다시 가야 할 때
    }

    /**
     * 로그인 처리
     * @param userId
     * @param password
     * @param request
     * @return
     */
    @PostMapping("/loginProcess")
    public String loginProcess(
            @RequestParam(value = "userId", defaultValue = "") String userId,
            @RequestParam(value = "password", defaultValue = "") String password,
            HttpServletRequest request
    ) {
        if (!userId.equals("") && !password.equals("")) {
            MemberVO memberVO = new MemberVO();
            memberVO.setUserId(userId);
            memberVO.setPassword(password);

            Boolean result = memberService.loginProcess(memberVO, request);

            logger.info("로그인 -{}", result);

            if (result == false) {
                return "redirect:/member/login";
            }

            return "redirect:/board/list";
        }

        return "redirect:/member/login";
    }

    /**
     * 아이디 찾기 페이지
     * @return
     */
    @GetMapping("/find_id")
    public String findId() {
        return "member/find_id";
    }

    @PostMapping("/find_id")
    public ModelAndView findIdPost(   // public String findIdPost
                                      @RequestParam(value = "name", defaultValue = "") String name,
                                      @RequestParam(value = "email", defaultValue = "") String email,
                                      //HttpServletResponse response
                                      ModelAndView mav
    ) throws IOException {

        if (!name.equals("") && !email.equals("")) {
            // 그냥 넘길 수 없어서 MemberVO 생성
            MemberVO memberVO = new MemberVO();

            // VO에 이름과 이메일 넣기
            memberVO.setName(name);
            memberVO.setEmail(email);

            // 쿼리를 던지고 String 타입의 아이디 받기
            String id = memberService.findUserId(memberVO);

            // 로그 출력
            logger.info("찾은 id -{}", id);

            // 찾은 값 사용자에게 보여줌 (파라미터 HttpServletResponse 이용)
            //response.setContentType("text/html; charset=UTF-8"); // html 타입으로 바로 받을 것
            //response.setCharacterEncoding("UTF-8");
            //PrintWriter out = response.getWriter();
            //out.println("<script>alert('찾으시는 ID는 " + id + "입니다.'); location.href='/member/login';</script>"); // 자바스크립트 코드 그대로 쓰기
            //out.flush(); // 내용 삭제

            if (name == null) {
                // 찾는 아이디가 없음
                mav.addObject("data", new Message("찾으시는 게정이 없습니다.", "/member/login"));
                mav.setViewName("message/message");
                return mav;
            } else {
                // 찾는 아이디가 있음 (보안을 위해 'ab**'처럼 일부 아이디 가려서)
                // 리턴된 아이디의 전체 길이를 파악한 후
                int idLength = id.length(); // 4
                // 전체 아이디에서 앞 두 글자만 남겨놓자
                id = id.substring(0, idLength-2); // ab
                id += "**"; // ab**

                logger.info("id 마스킹 -{}", id);

                mav.addObject("data", new Message(name + "님이 찾으시는 아이디는 " + id + "입니다.", "/member/login"));
                mav.setViewName("message/message");
                return mav;
            }
        }

        mav.addObject("data", new Message("이름과 이메일을 확인하세요.", "/member/login"));
        mav.setViewName("message/message");
        return mav;
    }

    /**
     * 비밀번호 찾기 페이지
     * @return
     */
    @GetMapping("/find_pw")
    public String findPw() {
        return "member/find_pw";
    }

    /**
     * 비밀번호 찾아서 바꾸기
     * @param userId
     * @param name
     * @param email
     * @param mav
     * @return
     */
    @PostMapping("/find_pw")
    public ModelAndView findPwPost(
            @RequestParam(value = "userId", defaultValue = "") String userId,
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "email", defaultValue = "") String email,
            ModelAndView mav
    ) {
        if (!userId.equals("") && !name.equals("") && !email.equals("")) {
            MemberVO memberVO = new MemberVO();
            memberVO.setUserId(userId);
            memberVO.setName(name);
            memberVO.setEmail(email);

            String id = memberService.findPassword(memberVO);

            if(id == null){
                mav.addObject("data", new Message("찾으시는 계정이 없습니다", "/member/find_pw"));
                mav.setViewName("message/message");
                return mav;
            }else{
                String pw = Util.generateRandomString(10);
                memberVO.setPassword("1111");
                memberService.updatePassword(memberVO);
                mav.addObject("data", new Message("변경된 비밀번호는 " + pw + "입니다", "/member/login"));
                mav.setViewName("message/message");
                return mav;
            }
        }

        mav.addObject("data", new Message("입력 정보를 확인하세요.", "/member/find_pw"));
        mav.setViewName("message/message");
        return mav;
    }

    /**
     * 로그아웃
     * @param request
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // false : 세션을 신규로 생성하지 않게
        if (session != null) { // 로그인 되어 있으면
            session.invalidate(); // 세션 정보 삭제
        }

        return "redirect:/member/login";
    }

}

