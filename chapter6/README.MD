# 어노테이션

 - @Autowired 를 스프링 4.3이후부터는 Contructor Inject를 권장 
  ```java
    // 기존 
   public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberMapper memberMapper;

    }
    
    // 다음과 같이 변경   
    public class MemberServiceImpl implements MemberService {

    private MemberMapper memberMapper;

    public MemberServiceImpl(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }
}


  ```
  
  ---
