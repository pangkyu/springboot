package com.example.chapter6.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SearchHelper {

    @ApiModelProperty(example = "1000") // Api 문서에서 파라미터의 기본 값 설정
    private int srchCode = 1000;
    @ApiModelProperty(example = "title")
    private String srchType;
    @ApiModelProperty(example = "검색어")
    private String srchKeyword;

    // 한 페이지에 출력될 row 갯수
    private int pageSize = 10;
    // 한 페이지에 페이징 번호가 몇개까지 출력되게 할지
    private int blockSize = 10;
    // 현재 페이지 번호
    @ApiModelProperty(example = "1")
    private int page = 1;
    // 현재 블록 번호
    private int block = 1;
    // 전체 row 갯수
    private int totalRowCnt;
    // 전체 페이지 수
    private int totalPageCnt;
    // 총 블록 수
    private int totalBlockCnt;
    // 블록 시작 페이지
    private int startPage = 1;
    // 블록 마지막 페이지
    private int endPage = 1;
    // sql 시작 index
    private int startIndex = 0;
    // 이전 블록의 마지막 페이지
    private int prevBlockPage;
    // 다음 블록의 시작 페이지
    private int nextBlockPage;

//    파라미터로 row 갯수를 카운트해서 현재 페이지 번호를 넣는다.
    public SearchHelper(int totalRowCnt, int page){
        // 현재 페이지 번호
        this.page = page;
        // 전체 row 갯수
        this.totalRowCnt = totalRowCnt;
        // 전체 페이지 수 = 총 row의 수 / 한 페이지에 출력될 row 수
        totalPageCnt = (int) Math.ceil(totalRowCnt * 1.0 / pageSize);
        // 게시물이 0개일 때, 첫번째 페이지는 있어야 하므로
        if(totalPageCnt == 0) totalPageCnt = 1;
        // 총 블록 수 = 총 페이지 수 / 한 블록이 출력하는 페이지 수
        totalBlockCnt = (int) Math.ceil(totalPageCnt * 1.0 / blockSize);
        // 게시물이 0개일 때, 첫번째 블록은 있어야 하므로
        if(totalBlockCnt == 0) totalBlockCnt = 1;
        // 현재 블록 위치 = 현재 페이지 번호 / 한 블록이 출력하는 페이지 수
        block = (int) Math.ceil(page * 1.0 / blockSize);
        // 블록 시작 페이지
        startPage = (block - 1) * blockSize + 1;
        // 블록 마지막 페이지
        endPage = startPage + blockSize - 1;

        // endPage가 totalPageCnt보다 크면 endPage에 totalPageCnt를 삽입
        if(endPage > totalPageCnt) endPage = totalPageCnt;

        // 이전 블록 클릭 시, 이전 블록의 마지막 페이지로
        prevBlockPage = (block * blockSize) - blockSize;

        // 이전 블록 클릭 시, 선택될 페이지 번호가 1보다 작으면 prevBlockPage에 1 삽입
        if(prevBlockPage < 1) prevBlockPage = 1;

        // 다음 블록 클릭 시, 다음 블록의 첫 페이지로
        nextBlockPage = (block * blockSize) + 1;

        // 다음 블록 클릭 시, 선택될 페이지 번호가 totalPageCnt보다 크면 nextBlockPage에 totalPageCnt 삽입
        if(nextBlockPage > totalPageCnt) nextBlockPage = totalPageCnt;

        // 첫 페이지의 index
        startIndex = (page - 1) * pageSize;

    }

}
