package com.example.demo.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/board")
public class BoardController {
    @RequestMapping("/list")
    public String boardlist(){
        return "board/list";
    }
    @RequestMapping("/view")
    public String boardview(){
        return "board/view";
    }
    @RequestMapping("/write")
    public String boardwrite(){
        return "board/write";
    }
}


