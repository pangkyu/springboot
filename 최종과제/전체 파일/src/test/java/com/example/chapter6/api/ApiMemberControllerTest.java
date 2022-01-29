package com.example.chapter6.api;

import com.example.chapter6.exception.UserNotFoundException;
import com.example.chapter6.model.MemberVO;
import com.example.chapter6.payload.request.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiMemberControllerTest {

    @Autowired
    MockMvc mvc;

    private String accessToken;
    private String tokenType;
    private String refreshToken;

    @Autowired
    WebApplicationContext context;

    @BeforeEach
    public void 로그인() throws Exception{

        this.mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();

        LoginRequest loginRequest = new LoginRequest("abcd", "abcd1234!");
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(loginRequest);

        MvcResult result = mvc.perform(post("/api/member/login")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer "))
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.accessToken").exists())
                .andDo(print())
                .andReturn();

        this.accessToken = (String) new JacksonJsonParser().parseMap(result.getResponse().getContentAsString()).get("accessToken");
        this.tokenType = (String) new JacksonJsonParser().parseMap(result.getResponse().getContentAsString()).get("tokenType");
        this.refreshToken = (String) new JacksonJsonParser().parseMap(result.getResponse().getContentAsString()).get("refreshToken");
    }

    @Test
    @DisplayName(value = "로그아웃")
    void logout() throws Exception {
        mvc.perform(get("/api/member/logout")
                        .header(HttpHeaders.AUTHORIZATION, tokenType + accessToken))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Disabled
    void loginProcess() {
    }

    @Test
    @DisplayName(value = "토큰 갱신")
    void regenToken() throws Exception{
        mvc.perform(get("/api/member/regenToken")
                        .header(HttpHeaders.AUTHORIZATION, tokenType + accessToken)
                        .param("refreshToken", refreshToken))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Disabled
    void apiTest() {
    }

    @Test
    @DisplayName(value = "아이디 중복 체크")
    void existId() throws Exception {
        mvc.perform(get("/api/member/exist/id/abcd"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName(value = "이메일 중복 체크")
    void existEmail() throws Exception {
        mvc.perform(get("/api/member/exist/email/abcd@gmail.com"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Disabled
    @DisplayName(value = "아이디 찾기")
    void findId() throws Exception {
        mvc.perform(get("/api/member/find/id/홍길동/abcd@gmail.com"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Disabled
    @DisplayName(value = "비밀번호 찾기")
    void findPassword() throws Exception {
        mvc.perform(get("/api/member/find/password/오유리/junit@gmail.com/junit"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Disabled
    @DisplayName(value = "회원가입")
    void memberJoin() throws Exception {
        MemberVO memberVO = new MemberVO();
        memberVO.setName("오유리");
        memberVO.setUserId("junit");
        memberVO.setEmail("junit@gmail.com");
        memberVO.setPassword("junit1234!");

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(memberVO);

        mvc.perform(post("/api/member/join")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}