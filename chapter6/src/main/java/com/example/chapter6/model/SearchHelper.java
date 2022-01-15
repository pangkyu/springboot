package com.example.chapter6.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SearchHelper {
    private String srchType;
    private String srchKeyword;
    private int page = 1;
    private int startIndex = 0;
    private int pageSize = 10;
}
