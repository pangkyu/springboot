package com.example.chapter6.board.serviceImpl;

import com.example.chapter6.board.service.BoardService;
import com.example.chapter6.mapper.BoardMapper;
import com.example.chapter6.model.BoardVO;
import com.example.chapter6.model.SearchHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.*;

@Service
public class BoardServiceImpl implements BoardService {

    private static final Logger logger = LoggerFactory.getLogger(BoardServiceImpl.class);

    private final BoardMapper boardMapper;

    public BoardServiceImpl(BoardMapper boardMapper){
        this.boardMapper = boardMapper;
    }

    // 게시물 저장
    @Override
    public void insertBoardVO(BoardVO boardVO) throws Exception {
        boardMapper.insertBoardVO(boardVO);
    }

    // 게시물 목록
    @Override
    public HashMap<String, Object> selectBoardVO(SearchHelper searchHelper) throws Exception {

        HashMap<String, Object> resultMap = new HashMap<>();

        int totalCnt = boardMapper.countBoardVO(searchHelper);

        int code = searchHelper.getSrchCode();
        String srchType = searchHelper.getSrchType();
        String srchKeyword = searchHelper.getSrchKeyword();

        searchHelper = new SearchHelper(totalCnt, searchHelper.getPage());

        searchHelper.setSrchCode(code);
        searchHelper.setSrchType(srchType);
        searchHelper.setSrchKeyword(srchKeyword);

        List<BoardVO> list = boardMapper.selectBoardVO(searchHelper);

        resultMap.put("list", list);
        resultMap.put("searchHelper", searchHelper);

        return resultMap;
    }

    // 게시물 조회
    @Override
    public Optional<BoardVO> selectBoardVOById(int id) throws Exception {
        Optional<BoardVO> isExist = boardMapper.selectBoardVOById(id);
        if(isExist.isPresent()){
            boardMapper.updateCount(id);
            return isExist;
        }else{
            throw new NoSuchElementException();
        }
    }

    // 게시물 수정
    @Override
    public void updateBoardVO(BoardVO boardVO) throws Exception {
        Optional<BoardVO> isExist = boardMapper.selectBoardVOById(boardVO.getId());
        if(isExist.isPresent())
            boardMapper.updateBoardVO(boardVO);
        else
            throw new Exception("수정하려는 게시물이 존재하지 않습니다.");
    }

    // 게시물 삭제
    @Override
    public void deleteById(int id) throws Exception {
        Optional<BoardVO> isExist = boardMapper.selectBoardVOById(id);
        if(isExist.isPresent())
            boardMapper.deleteById(id);
        else
            throw new Exception("삭제하려는 게시물이 존재하지 않습니다.");
    }

    @Override
    public Map<String, String> formValidation(Errors errors){
        Map<String, String> result = new HashMap<>();

        for(FieldError error : errors.getFieldErrors()){
            String validKeyName = String.format("valid_%s", error.getField());
            result.put(validKeyName, error.getDefaultMessage());
        }

        return result;
    }

}
