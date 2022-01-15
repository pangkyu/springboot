package com.example.chapter6.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

@Controller
@RequestMapping("/board")
public class BoardController {

    @RequestMapping("/list")
    public String boardList() {

        return "board/list";
    }

    @RequestMapping("/view")
    public String boardView() {
        return "board/view";
    }

    @RequestMapping("/write")
    public String boardWrite() {
        return "board/write";
    }

}
