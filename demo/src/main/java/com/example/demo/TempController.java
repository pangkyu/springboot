package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TempController {

    @RequestMapping("/temp")
    public String temp(
            @RequestParam(value = "code", defaultValue = "0") String code,
            @RequestParam(value = "id", defaultValue = "0") int id
            //많을때는 @RequestParam HashMap<String, String> hashMap 사용
            // hashMap.get("id") 식으로. 하지만 어떤 파라미터들이 들어가는지 정확히 알기 어려워서 비추천
    ){
        System.out.println("=======");
        System.out.println(code);
        System.out.println(id);
        System.out.println("=======");

        return "temp";
    }
}
