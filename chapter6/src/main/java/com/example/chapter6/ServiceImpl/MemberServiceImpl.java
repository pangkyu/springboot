package com.example.chapter6.ServiceImpl;

import com.example.chapter6.mapper.MemberMapper;
import com.example.chapter6.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;

public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberMapper memberMapper;
}
