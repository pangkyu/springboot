package com.example.chapter6.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class BoardVO{

    private int id = 0;
    private int code;
    private String title;
    private String content;
    private String regId;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private int count;

}
