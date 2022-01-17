package com.example.chapter6.Service;

import com.example.chapter6.model.MemberVO;
import org.springframework.validation.Errors;

import java.util.Map;

public interface MemberService {
    Map<String, String> formValidation(Errors errors);
    void insertMember(MemberVO memberVO) throws Exception;

    Boolean duplicateId(String id);

    Boolean duplicateEmail(String email);
}
