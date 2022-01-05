# 어노테이션

```java
package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HelloController {

   
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String hello(){
        System.out.println("hello 안녕");
        return "hello"; // hello.html
    }
}



```
 - get => 사용자가 url 직접쳐서 들어옴 저기서 url 더 넣고싶으면 value = "/", "/main" .... 으로 추가 
 - 축약으로 하고싶다? => @GetMapping, @PostMapping 으로
