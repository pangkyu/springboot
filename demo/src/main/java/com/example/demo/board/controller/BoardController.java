package com.example.demo.board.controller;

import com.example.demo.board.model.BoardVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/board")
public class BoardController {

    @RequestMapping("/list")
    public String boardlist(
            @RequestParam(value = "srchType", defaultValue = "") String srchType,
            @RequestParam(value = "srchKeyword", defaultValue = "") String srchKeyword,
            Model model){
        ArrayList<BoardVO> list = new ArrayList<>();
        for(int i = 0; i<=10; i++){
            BoardVO boardVO = generate(i, i+"번 게시물", i+"번 게시물 내용", "관리자", 100 );
            list.add(boardVO);
        }

        ArrayList<BoardVO> result = new ArrayList<>();
        if (srchType.equals("title")) {
            for(BoardVO b : list){
                if(b.getTitle().contains(srchKeyword)) result.add(b);
            }
        }else if(srchType.equals("content")){
            for(BoardVO b : list){
                if(b.getContent().contains(srchKeyword)) result.add(b);
            }
        }else{
            result.addAll(list);
        }
        model.addAttribute("srchType", srchType);
        model.addAttribute("srchKeyword", srchKeyword);
        model.addAttribute("result", result);
        return "board/list";
    }
    @RequestMapping("/view")
    public String boardView(
            @RequestParam(value = "id", defaultValue = "1") int id,
            @RequestParam(value = "srchType", defaultValue = "") String srchType,
            @RequestParam(value = "srchKeyword", defaultValue = "") String srchKeyword,
            Model model
    ) {

        ArrayList<BoardVO> list = new ArrayList<>();
        for(int i = 1; i <= 10; i++) {
            BoardVO boardVO = generate(i, i + "번 게시물", i + "번 게시물 내용", "관리자", 100);
            list.add(boardVO);
        }

        model.addAttribute("srchType", srchType);
        model.addAttribute("srchKeyword", srchKeyword);
        model.addAttribute("data", list.get(id));

        return "board/view";
    }
    @RequestMapping("/write")
    public String boardwrite(){
        return "board/write";
    }

    public BoardVO generate(int id, String title, String content, String write, int count){

        BoardVO boardVO = new BoardVO();
        boardVO.setId(id);
        boardVO.setTitle(title);
        boardVO.setContent(content);
        boardVO.setWriter(write);
        boardVO.setRegDate(LocalDateTime.of(2021, 1, 1, 12, 30, 00 ));
        boardVO.setCount(count);
        return boardVO;
    }
}


