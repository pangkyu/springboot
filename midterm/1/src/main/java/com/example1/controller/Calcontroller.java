package com.example1.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/board")
public class Calcontroller {
    @RequestMapping("/midterm")
    public String midString(Model model,
                            @RequestParam(value="first", defaultValue = "0") int first,
                            @RequestParam(value="second", defaultValue = "0") int second,
                            @RequestParam(value="operator", defaultValue = "+")String operator){
        int result = 0;

        if(operator.equals("+")){
            result = first + second;
        }
        else if(operator.equals("-")){
            result = first - second;
        }
        else if(operator.equals("*")){
            result = first * second;
        }
        else if(operator.equals("/")){
            result = first / second;
        }
        return "board/midterm";
    }

    @RequestMapping("/midterm2")
    public String midResultString(Model model,
                                  @RequestParam(value="first", defaultValue = "0") int first,
                                  @RequestParam(value="second", defaultValue = "0") int second,
                                  @RequestParam(value="operator", defaultValue = "+")String operator){
        int result = 0;
        if(operator.equals("+")){
            result = first + second;
        }
        else if(operator.equals("-")){
            result = first - second;
        }
        else if(operator.equals("*")){
            result = first * second;
        }
        else if(operator.equals("/")){
            result = first / second;
        }

        model.addAttribute("first",first);
        model.addAttribute("second",second);
        model.addAttribute("operator",operator);
        model.addAttribute("result",result);


        return "board/midterm2";
    }


}
