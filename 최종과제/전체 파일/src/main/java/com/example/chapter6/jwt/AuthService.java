package com.example.chapter6.jwt;

import com.example.chapter6.Util.ExceptionMessage;
import com.example.chapter6.exception.UserNotFoundException;
import com.example.chapter6.model.RefreshTokenVO;
import com.example.chapter6.payload.response.JwtAuthenticationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public AuthService(JwtTokenProvider jwtTokenProvider, RefreshTokenService refreshTokenService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    //    토큰 생성
    public String generateToken(String userId){
        return jwtTokenProvider.generateToken(userId);
    }

//    토큰으로 아이디 반환
    public String getUserIdFromJWT(String token){
        return jwtTokenProvider.getUserIdFromJWT(token);
    }

//    토큰으로 만료일 반환
    public Date getTokenExpiryFromJWT(String token){
        return jwtTokenProvider.getTokenExpiryFromJWT(token);
    }

//    리프레시 토큰 갱신
    public JwtAuthenticationResponse regenToken(String refreshTokenRequest) {
        Optional<RefreshTokenVO> refreshTokenExistCheck = refreshTokenService.selectByRefreshToken(refreshTokenRequest);

        if(refreshTokenExistCheck.isPresent()){
            refreshTokenService.verifyExpiration(refreshTokenExistCheck.get());

            RefreshTokenVO generateTokens = refreshTokenService.updateRefreshToken(refreshTokenExistCheck.get().getMemberId());

            String accessToken = generateToken(refreshTokenExistCheck.get().getUserId());

            JwtAuthenticationResponse response = new JwtAuthenticationResponse();
            response.setExpiryDuration(generateTokens.getExpiryDate().toEpochMilli());
            response.setAccessToken(accessToken);
            response.setRefreshToken(generateTokens.getRefreshToken());

            return response;
        }else{
            throw new UserNotFoundException("refresh token 정보가 없습니다.");
        }
    }
}
