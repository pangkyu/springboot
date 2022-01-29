package com.example.chapter6.mapper;

import com.example.chapter6.model.RefreshTokenVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface RefreshTokenMapper {

//    토큰을 최초로 생성
    RefreshTokenVO insertRefreshToken(RefreshTokenVO refreshTokenVO);

//    member id 존재 여부 체크
    boolean existMemberId(int id);

//    토큰 갱신 및 카운트 증가
    void updateRefreshToken(RefreshTokenVO refreshTokenVO);

//    토큰으로 사용자 정보 조회
    Optional<RefreshTokenVO> selectByRefreshToken(String refreshToken);
}
