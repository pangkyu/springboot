package com.example.chapter6.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RefreshTokenVO {

    private String refreshToken;
    private int refreshCount = 0;
    private Instant expiryDate;
    private int id;
    private String userId;
    private int memberId;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
