package com.example.chapter6.mapper;

import com.example.chapter6.model.MemberVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

    void insertMember(MemberVO memberVO);

    Boolean duplicateId(String id);

    Boolean duplicateEmail(String email);
}

