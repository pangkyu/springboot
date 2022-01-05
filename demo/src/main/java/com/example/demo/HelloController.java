package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HelloController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String hello(Model model){

        Map<String, Object> map = new HashMap<>();
        map.put("name", "이름");
        map.put("age", 19);
        map.put("tel", "010-1111-2222");
        model.addAttribute("data", map);

        List<Product> productList = new ArrayList<>();
        productList.add(new Product("사과", 1000));
        productList.add(new Product("딸기", 2000));
        productList.add(new Product("포도", 1500));
        productList.add(new Product("복숭아", 3000));

        model.addAttribute("product", productList);
        return "hello"; // hello.html
    }
}
