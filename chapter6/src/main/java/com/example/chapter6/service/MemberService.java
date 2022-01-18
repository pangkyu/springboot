package com.example.chapter6.service;

import com.example.chapter6.model.MemberVO;
import org.springframework.validation.Errors;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface MemberService {

    Boolean duplicateId(String id);
    Boolean duplicateEmail(String email);

    void insertMember(MemberVO memberVO) throws Exception; // 예외 발생 시 throws 처리 (권장)

    Boolean loginProcess(MemberVO memberVO, HttpServletRequest request);

    String findUserId(MemberVO memberVO);

    String findPassword(MemberVO memberVO);

    void updatePassword(MemberVO memberVO);

    Map<String, String> formValidation(Errors errors);

}
