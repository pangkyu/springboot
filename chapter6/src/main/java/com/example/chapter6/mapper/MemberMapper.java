package com.example.chapter6.mapper;

import com.example.chapter6.model.MemberVO;
import org.apache.ibatis.annotations.Mapper;

import java.lang.reflect.Member;

@Mapper
public interface MemberMapper {

    Boolean duplicateId(String id);
    Boolean duplicateEmail(String email);

    void insertMember(MemberVO memberVO);

    MemberVO loginProcess(MemberVO memberVO);

    String findUserId(MemberVO memberVO);

    String findPassword(MemberVO memberVO);

    void updatePassword(MemberVO memberVO);

}

