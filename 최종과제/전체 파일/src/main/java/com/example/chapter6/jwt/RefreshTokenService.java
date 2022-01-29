package com.example.chapter6.jwt;

import com.example.chapter6.exception.RefreshTokenExpirationException;
import com.example.chapter6.mapper.RefreshTokenMapper;
import com.example.chapter6.model.RefreshTokenVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class RefreshTokenService {

    private RefreshTokenMapper refreshTokenMapper;

    @Value("${app.jwt.token.refresh.duration}")
    private long refreshTokenDurationMs;

    public RefreshTokenService(RefreshTokenMapper refreshTokenMapper) {
        this.refreshTokenMapper = refreshTokenMapper;
    }

    //    토큰을 최초로 생성
    public RefreshTokenVO insertRefreshToken(int id){
        RefreshTokenVO refreshTokenVO = new RefreshTokenVO();
        refreshTokenVO.setRefreshToken(UUID.randomUUID().toString());
        refreshTokenVO.setMemberId(id);
        refreshTokenVO.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshTokenMapper.insertRefreshToken(refreshTokenVO);
        return refreshTokenVO;
    }

    //    member id 존재 여부 체크
    public boolean existMemberId(int id){
        return refreshTokenMapper.existMemberId(id);
    }

    //    토큰 갱신 및 카운트 증가
    public RefreshTokenVO updateRefreshToken(int memberId){
        RefreshTokenVO refreshTokenVO = new RefreshTokenVO();
        refreshTokenVO.setRefreshToken(UUID.randomUUID().toString());
        refreshTokenVO.setMemberId(memberId);
        refreshTokenVO.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshTokenMapper.updateRefreshToken(refreshTokenVO);
        return refreshTokenVO;
    }

    //    토큰으로 사용자 정보 조회
    public Optional<RefreshTokenVO> selectByRefreshToken(String refreshToken){
        return refreshTokenMapper.selectByRefreshToken(refreshToken);
    }

    //    리프레시 토큰 만료시간 검증
    public void verifyExpiration(RefreshTokenVO refreshTokenVO){
        // true면 만료시간 경과
        if(refreshTokenVO.getExpiryDate().compareTo(Instant.now()) < 0){
            throw new RefreshTokenExpirationException(refreshTokenVO.getRefreshToken(), "토큰 유효기간 만료");
        }
    }
}
