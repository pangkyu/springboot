package com.example.chapter6.board.serviceImpl;

import com.example.chapter6.board.service.BoardService;
import com.example.chapter6.mapper.BoardMapper;
import com.example.chapter6.model.BoardVO;
import com.example.chapter6.model.SearchHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class BoardServiceImplTest {

    @Autowired
    BoardService boardService;

    @Test
    @DisplayName(value = "게시물 추가")
    void insertBoardVO() {

    }

    @Test
    @DisplayName(value = "게시물 조회")
    void selectBoardVO() throws Exception {
        SearchHelper searchHelper = new SearchHelper();
        searchHelper.setSrchCode(1000);
        System.out.println(boardService.selectBoardVO(searchHelper));
    }

    @Test
    @DisplayName(value = "id로 게시물 조회")
    void selectBoardVOById() throws Exception {
    }

    @Test
    @DisplayName(value = "게시물 수정")
    void updateBoardVO() throws Exception {
        BoardVO boardVO = new BoardVO();
        boardVO.setId(10000);
        boardService.updateBoardVO(boardVO);
    }

    @Test
    @DisplayName(value = "게시물 삭제")
    void deleteById() {
    }

    @Test
    void formValidation() {
    }
}