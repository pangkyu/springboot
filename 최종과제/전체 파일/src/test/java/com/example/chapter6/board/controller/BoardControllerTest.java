package com.example.chapter6.board.controller;

import com.example.chapter6.board.service.BoardService;
import com.example.chapter6.model.MemberVO;
import com.example.chapter6.model.SearchHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.FileInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class BoardControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    BoardService boardService;

    private MockHttpSession session;

    @BeforeEach
    void 셋업(){
        session = new MockHttpSession();
        MemberVO memberVO = new MemberVO();
        memberVO.setLevel(10);
        memberVO.setEmail("mail@gmail.com");
        memberVO.setUserId("admin");
        memberVO.setName("관리자");
        session.setAttribute("memberVO", memberVO);
    }

    @Test
    @DisplayName(value = "게시물 목록")
    void 게시물_목록() throws Exception {
        SearchHelper searchHelper  = new SearchHelper();
        searchHelper.setSrchCode(1000);
        mvc.perform(MockMvcRequestBuilders.get("/board/list")
                        .param("srchCode", "1000").session(session))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName(value = "게시물 조회")
    void 게시물_조회() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/board/view")
                        .param("srchCode", "1000")
                        .param("id", "36")
                        .session(session))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName(value = "게시물 작성 페이지")
    void 게시물_작성_페이지() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/board/write")
                .param("srchCode", "1000").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("board/write"))
                .andDo(print());
    }

    @Test
    @Disabled
    @DisplayName(value = "게시물 저장")
    void 게시물_저장() throws Exception {

        FileInputStream file = new FileInputStream("C:/Users/USER/Desktop/할 거 목록.txt");
        MockMultipartFile multipartFile = new MockMultipartFile("file", "할 거 목록.txt", "plain/text", file);

        mvc.perform(MockMvcRequestBuilders.multipart("/board/save")
                        .file(multipartFile)
                        .param("title", "title")
                        .param("content", "content")
                        .param("regId", "test JUnit")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andDo(print());
        // 혹시 redirect이면 andExpect(redirectUrl("경로"))
    }


    @Test
    @Disabled
    @DisplayName(value = "게시물 수정 페이지")
    void 게시물_수정_페이지() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/board/modify")
                        .param("srchCode", "1000")
                        .param("id", "36")
                        .session(session))
                .andExpect(status().isOk())
                .andDo(print());
    }
}