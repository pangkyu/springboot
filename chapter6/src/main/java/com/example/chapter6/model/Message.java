package com.example.chapter6.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Message {

    private String message = ""; // String 타입의 message / "" 기본 메시지는 없게
    private String href = "/"; // 이동해야 할 경로의 링크 / "/" 기본 링크 루트로 가게 해도 됨

    public Message(String message, String href) {
        this.message = message;
        this.href = href;
    }
}
