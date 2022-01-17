package com.example.chapter6.ServiceImpl;

import com.example.chapter6.Service.MemberService;
import com.example.chapter6.mapper.MemberMapper;
import com.example.chapter6.model.MemberVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Service
public class MemberServiceImpl implements MemberService {

    private MemberMapper memberMapper;

    public MemberServiceImpl(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }


    /*
    아이디 중복 체크
    @Param id
    @return
     */
    @Override
    public Boolean duplicateId(String id) {
        Boolean res = memberMapper.duplicateId(id);
        return res ? true : false;
    }

    /*
    이메일 중복체크
    @param email
    @return
     */
    @Override
    public Boolean duplicateEmail(String email) {
        Boolean res = memberMapper.duplicateEmail(email);
        return res ? true : false;

    }

    /*
    회원가입 폼 검증
    @param errors
    @return
     */
    @Override
    public Map<String, String> formValidation(Errors errors) {
        Map<String, String> result = new HashMap<>();

        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            result.put(validKeyName, error.getDefaultMessage());
        }

        return result;
    }

    /*
    회원가입처리
    @param memberVO
     */
    @Override
    public void insertMember(MemberVO memberVO) throws  Exception{
        memberMapper.insertMember(memberVO);

    }
}
