package com.example.chapter6.api;

import com.example.chapter6.Util.ExceptionMessage;
import com.example.chapter6.Util.Util;
import com.example.chapter6.event.OnLogoutSuccessEvent;
import com.example.chapter6.exception.BadRequestException;
import com.example.chapter6.exception.InsertFailException;
import com.example.chapter6.exception.ResourceAlreadyInUseException;
import com.example.chapter6.exception.UserNotFoundException;
import com.example.chapter6.jwt.AuthService;
import com.example.chapter6.member.service.MemberService;
import com.example.chapter6.model.MemberVO;
import com.example.chapter6.payload.request.LoginRequest;
import com.example.chapter6.payload.response.ApiResponse;
import com.example.chapter6.payload.response.JwtAuthenticationResponse;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/member")
public class ApiMemberController {

    private static final Logger logger = LoggerFactory.getLogger(ApiMemberController.class);

    private final MemberService memberService;
    private final AuthService authService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public ApiMemberController(MemberService memberService, AuthService authService, ApplicationEventPublisher applicationEventPublisher) {
        this.memberService = memberService;
        this.authService = authService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @GetMapping("/logout")
    public ApiResponse logout(
            HttpServletRequest request
    ){
        String accessToken = request.getHeader("Authorization");
        accessToken = accessToken.replace("Bearer ", "");
        // 토큰 검증...
        String res = authService.getUserIdFromJWT(accessToken);
        OnLogoutSuccessEvent event = new OnLogoutSuccessEvent(res, accessToken);
        applicationEventPublisher.publishEvent(event);

        return new ApiResponse(true, "완료");
    }

//    로그인 처리
    @PostMapping("/login")
    public ResponseEntity loginProcess(
            @RequestBody @Valid LoginRequest loginRequest
    ) throws Exception {
        MemberVO memberVO = new MemberVO();
        memberVO.setUserId(loginRequest.getUserId());
        memberVO.setPassword(loginRequest.getPassword());

        JwtAuthenticationResponse result = memberService.loginProcess(memberVO);

        return ResponseEntity.ok(result);
    }

    // 토큰 갱신
    @GetMapping("/regenToken")
    @ApiOperation(value = "토큰 갱신", notes = "갱신할 때 리프레시 토큰을 파라미터로 넣습니다.")
    @ApiImplicitParam(name = "refreshToken", value = "리프레시 토큰 값", required = true)
    public ResponseEntity regenToken(
            @RequestParam(value = "refreshToken", defaultValue = "") String refreshToken
    ){
        if(refreshToken.equals("")){
            throw new BadRequestException("리프레시 토큰이 없습니다.");
        }else {
            JwtAuthenticationResponse response = authService.regenToken(refreshToken);
            return ResponseEntity.ok(response);
        }
    }


    @GetMapping("/apiTest")
    @ApiIgnore // Api 문서에서 제외
    public ApiResponse apiTest(
        HttpServletRequest request
    ){
        String token = request.getHeader("Authorization");
        token = token.replace("Bearer ", "");
        logger.info("토큰으로 반환된 ID - {}", authService.getUserIdFromJWT(token));
        logger.info("토큰으로 반환된 만료일 - {}", authService.getTokenExpiryFromJWT(token));
        return new ApiResponse(true, "완료");
    }

    // 아이디 중복 체크
    @GetMapping("/exist/id/{userId}")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 200, message = "정상적으로 아이디가 조회 되었을 때"),
            @io.swagger.annotations.ApiResponse(code = 409, message = "아이디 중복")
    })
    public ApiResponse existId(
            @PathVariable String userId
    ) throws Exception{
        if(userId.equals("")) {
            throw new BadRequestException(ExceptionMessage.EMPTY_USER_ID);
        }
        Boolean result = memberService.duplicateId(userId);

        // true로 리턴되면 아이디가 선점되어 있다.
        if(result) {
            throw new ResourceAlreadyInUseException("입력하신 아이디", "userId", userId);
        }

        return new ApiResponse(true, ExceptionMessage.AVAILABLE_ID);
    }

    // 이메일 중복 체크
    @GetMapping("/exist/email/{email}")
    public ApiResponse existEmail(
            @PathVariable String email
    ) throws Exception{
        if(email.equals("")) {
            throw new BadRequestException(ExceptionMessage.EMPTY_USER_EMAIL);
        }
        Boolean result = memberService.duplicateEmail(email);

        // true로 리턴되면 이메일이 선점되어 있다.
        if(result) {
            throw new ResourceAlreadyInUseException("입력하신 이메일", "email", email);
        }

        return new ApiResponse(true, ExceptionMessage.AVAILABLE_EMAIL);
    }

    // 아이디 찾기
    @GetMapping("/find/id/{name}/{email}")
    public ApiResponse findId(
            @PathVariable String name,
            @PathVariable String email
    ){
        if(!name.equals("") && !email.equals("")){
            MemberVO memberVO = new MemberVO();
            memberVO.setName(name);
            memberVO.setEmail(email);

            try {
                String id = memberService.findUserId(memberVO);
                if(id == null){
                    throw new UserNotFoundException(ExceptionMessage.NOT_FOUND_USER_ID);
                }
                int idLength = id.length();
                id = id.substring(0, idLength - 2);
                id += "**";
                return new ApiResponse(true, name + "님이 찾으시는 ID는 " + id + "입니다.");
            }catch (Exception e){
                throw new UserNotFoundException(ExceptionMessage.NOT_FOUND_USER_ID);
            }
        }
        throw new BadRequestException(ExceptionMessage.EMPTY_INFO);
    }

    @GetMapping("/find/password/{name}/{email}/{userId}")
    public ApiResponse findPassword(
            @PathVariable String name,
            @PathVariable String email,
            @PathVariable String userId
    ){
        if(!name.equals("") && !email.equals("") && !userId.equals("")){
            MemberVO memberVO = new MemberVO();
            memberVO.setName(name);
            memberVO.setEmail(email);
            memberVO.setUserId(userId);

            try {
                String id = memberService.findPassword(memberVO);
                if(id == null){
                    throw new UserNotFoundException(ExceptionMessage.NOT_FOUND_USER_ID);
                }

                String pw = Util.generateRandomString(6);
                memberVO.setPassword(pw);
                memberService.updatePassword(memberVO);
                return new ApiResponse(true, "변경된 비밀번호 - {" + pw + "}");
            }catch (Exception e){
                throw new UserNotFoundException(ExceptionMessage.NOT_FOUND_USER_ID);
            }
        }
        throw new BadRequestException(ExceptionMessage.EMPTY_INFO);
    }

    // 회원가입 처리
    @PostMapping("/join")
    public ApiResponse memberJoin(
        @RequestBody @Valid MemberVO memberVO,
        Errors errors
    ) throws Exception{
        HashMap<String, Object> errorMap = new HashMap<>();

        if (errors.hasErrors()){
            Map<String, String> validate = memberService.formValidation(errors);

            for(String key : validate.keySet()){
                errorMap.put(key, validate.get(key));
            }
            return new ApiResponse(false, ExceptionMessage.SAVE_FAIL, errorMap);
        }

        boolean idCheck = false;
        boolean emailCheck = false;
        try {
            idCheck = memberService.duplicateId(memberVO.getUserId());
            emailCheck = memberService.duplicateEmail(memberVO.getEmail());
        }catch (Exception e){
            throw new BadRequestException(ExceptionMessage.ERROR);
        }

        if(!idCheck && !emailCheck){
            try {
                memberService.insertMember(memberVO);
                return new ApiResponse(true, ExceptionMessage.JOIN_COMPLETE);
            }catch (Exception e){
                throw new InsertFailException(ExceptionMessage.SAVE_FAIL);
            }
        }
        throw new ResourceAlreadyInUseException("id 또는 email", "id/email", memberVO.getUserId() + "/" + memberVO.getEmail());
    }
}
