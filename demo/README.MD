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

---
 
# 타임리프와 html 

 컨트롤러에서 html 페이지로 데이터 넘길 때 => model 사용 
 
 ```java
 @Controller
public class HelloController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String hello(Model model){

        Map<String, Object> map = new HashMap<>();
        map.put("name", "이름");
        map.put("age", 19);
        map.put("tel", "010-1111-2222");
        model.addAttribute("data", map);
        return "hello"; // hello.html
    }
}
 ```
 
 ```html
 <!DOCTYPE html>
<html lang="ko" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<!--<p th:text ="${msg}"></p> -->
<div th:object="${data}">
    <p>이름 : <span th:text = "*{name}"></span></p>
    <p>나이 : <span th:text = "*{age}"></span></p>
    <p>전화번호 : <span th:text = "*{tel}"></span></p>
</div>
</body>
</html>
 ```
 if문
 ```html
<div th:object="${data}">
    <p>이름 : <span th:if = "*{name == '이름'}">이름님 반갑습니다</span></p>
    <p>이름 : <span th:if = "*{name.toString().equals('이름')}">이름님 반갑습니다</span></p>
    <p>나이 : <span th:if = "*{age>15}">15세 이상입니다</span></p>
    <p>나이 : <span th:unless = "*{age>15}">15세 이하입니다.</span></p> <!-- unless가 else역할 -->
    <p>나이 : <span th:text = "*{age>15} ? '15세 이상' : '15세 이하'">15세 이상입니다</span></p>
    <p>전화번호 : <span th:text = "*{tel}"></span></p>
</div>
 ```
 
  Thymeleaf에서 자주사용하는 문법 
   th:text ==> 텍스트출력 
   th:if ==> 변수값 비교
   th:unless ==> 변수값 비교(else역할)
   th:each ==> 반복문
   th:value ==> HTML 속성중 input, checkbox, radio, select 등의 값 지정
   th:utext ==> 변수가 텍스트가 이닌 html코드일 경우 사용 
   
   
   
   ---
  제이쿼리 설정
  1)
  static - js - 생성
  application.properties에 
     spring.mvc.static-path-pattern=/static/** 입력해야 static 인식. (안하면 404) 
     spring.resources.static-locations=classpath:/static/
     spring.resources.add-mappings=true
    
  2) 중앙집중형 
   webjars.org에서 bootstrap검색후 복사 
   build.gradle dependencies에 implementation '복사내용' 입력
   제이쿼리도 동일하게.
 
  버전에 상관없게 ==> https://mvnrepository.com/ 사이트에서 webjars 검색 
  
  
  ---
  
  ```java
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
  ```
```html
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<form action = "/temp" method = "get" enctype="multipart/form-data">
    <input type = "text" name = "code"><br/>
    <input type = "text" name = "id">
    <button type = "submit">확인</button>
</form>
</body>
</html>
```

 - @PathVariable => URI의 패턴에 구분자로 들어오는 값을 처리할 때 사용
  ex) 
        @RequestMapping("/temp/{code}/{id}")
        public String temp( @PathVariable String code, @PathVariable int id )  
        
        temp/9999/90 등으로입력하면 데이터 전달됨 
---

 # 퍼시스턴트 프레임워크 
 
  - ORM(Object Relational Mapping)은 자바에서 쿼리문을 수행하고 결과를 출력할 수 있도록 jdbc api를 제공 
  - ORM을 쓰면 직접적인 SQL문을 작성하지 않아도 되고, 객체와 데이터베이스의 데이터를 자동으로 매핑해준다. 
  - 쿼리문을 작성하지 않아도 되기때문에 비즈니스 로직에 집중할 수 있다. 

--- 
 - mybatis 문법
   - choose, when, otherwise ==> if-else 처럼 비교연산을 할 수 있는 태그 
   - collection : 참조할 배열 , item : 전달 받은 배열 이름을 아이템 태그의 값으로 대체 , index : 순번
   - open : 구문 시작시 삽입할 문자열, close : 구문종료시 삽입할 문자열, separator : 반복문 사이에 삽입할 구분 문자열 
   - 쿼리 작성시 <,>,&,|| 비교연산자 그냥 작성하면 xml오류 
  

---
 - 어노테이션 
   - service를 인터페이스로 생성했으니 구현재인 servicelmpl클래스를 생성, 서비스를 implement함 
   - 구현채안에서 @Autowired 어노테이션을 써서 dao의 의존성을 주입 
   - dao는 인터페이스로 지정하고 @Mapper 어노테이션을 적용하면 dao가 Mybatis와 연결됨

 
