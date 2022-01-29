package com.example.chapter6.mapper;

import com.example.chapter6.model.BoardVO;
import com.example.chapter6.model.SearchHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BoardMapperTest {

    @Autowired
    BoardMapper boardMapper;

    @Test
    void 게시물_목록_호출() throws Exception {
        BoardVO boardVO = new BoardVO();
        boardVO.setCode(1000);
        boardVO.setTitle("test ins");
        boardVO.setContent("내용");
        boardVO.setRegId("test JUnit");
        boardMapper.insertBoardVO(boardVO);
    }
}