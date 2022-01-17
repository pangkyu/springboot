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
   - 회원가입및 로그설정
     <logger name = "옵선 명" level = "로그레벨/> 형태로 설정 
     jdbc : log4jdbc 옵션 설정 
     jdbc.connection : 커넥션 open, close 정보
     jdbc.sqlonly : sql문 정보 
     jdbc.sqltiming : sql 실행 시간 
     jdbc.audit : ResultSet을 제외한 모든 JDBC 호출 정보
     jdbc.resultset : ResultSet을 포함한 jdbc 호출 정보 
     jdbc.resultsettable : 쿼리 결과를 테이블 형태로 출력 
