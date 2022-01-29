package com.example.chapter6.api;

import com.example.chapter6.Util.ExceptionMessage;
import com.example.chapter6.board.service.BoardService;
import com.example.chapter6.exception.BadRequestException;
import com.example.chapter6.exception.InsertFailException;
import com.example.chapter6.file.UploadFileService;
import com.example.chapter6.jwt.AuthService;
import com.example.chapter6.member.service.MemberService;
import com.example.chapter6.model.*;
import com.example.chapter6.payload.response.ApiResponse;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController // 리턴을 Json으로 함
@RequestMapping("/api/board")
public class ApiBoardController {

    private final Logger logger = LoggerFactory.getLogger(ApiBoardController.class);

    private final BoardService boardService;
    private final AuthService authService;
    private final UploadFileService uploadFileService;

    public ApiBoardController(BoardService boardService, AuthService authService, UploadFileService uploadFileService) {
        this.boardService = boardService;
        this.authService = authService;
        this.uploadFileService = uploadFileService;
    }

    // 게시물 목록
    @PostMapping("/list")
    @ApiOperation(value = "게시물 목록", notes = "게시물 목록을 불러옵니다. page 파라미터를 최소 1 이상으로 설정해서 실행하세요.") // Api 문서에서 설명글 추가
    public ResponseEntity boardList(
            @RequestBody SearchHelper searchHelper // Json을 파라미터로 받음
    ) throws Exception {
        HashMap<String, Object> result = boardService.selectBoardVO(searchHelper);

        logger.info(searchHelper.toString());

        return new ResponseEntity(result, HttpStatus.OK);
    }

    // 게시물 조회
    @GetMapping("/view/{id}")
    public ResponseEntity boardView(
            @PathVariable int id
    ) throws Exception {
        Optional<BoardVO> boardVO = boardService.selectBoardVOById(id);

        if(boardVO.isPresent()){
            return new ResponseEntity(boardVO, HttpStatus.OK);
        }else{
            throw new BadRequestException(ExceptionMessage.NOT_FOUND_ARTICLE);
        }
    }

    // 게시물 저장
    @PostMapping("/save") // Postman에서 post로 바꿔야함
    public ApiResponse boardSave(
            @RequestBody @Valid BoardVO boardVO, Errors errors,
            HttpServletRequest request
    ) throws Exception {

        String token = request.getHeader("Authorization");
        token = token.replace("Bearer ", "");
        String userId = authService.getUserIdFromJWT(token);

        if(!userId.equals(""))
            boardVO.setRegId(userId);
        else
            throw new InsertFailException(ExceptionMessage.NOT_FOUND_USER_ID);

        HashMap<String, Object> errorMap = new HashMap<>();

        if (errors.hasErrors()){
            Map<String, String> validate = boardService.formValidation(errors);

            for(String key : validate.keySet()){
                errorMap.put(key, validate.get(key));
            }
            return new ApiResponse(false, ExceptionMessage.SAVE_FAIL, errorMap);
        }

        try {
            boardService.insertBoardVO(boardVO);
        }catch (Exception e){
            throw new InsertFailException(ExceptionMessage.SAVE_FAIL);
        }
        return new ApiResponse(true, ExceptionMessage.SAVE_SUCCESS);
    }

    @PutMapping("/update")
    public ApiResponse boardUpdate(
            @RequestBody @Valid BoardVO boardVO, Errors errors,
            HttpServletRequest request
    ) throws Exception {

        String token = request.getHeader("Authorization");
        token = token.replace("Bearer ", "");
        String userId = authService.getUserIdFromJWT(token);

        if(!userId.equals(""))
            boardVO.setRegId(userId);
        else
            throw new BadRequestException(ExceptionMessage.NOT_FOUND_USER_ID);

        HashMap<String, Object> errorMap = new HashMap<>();

        if (errors.hasErrors()){
            Map<String, String> validate = boardService.formValidation(errors);

            for(String key : validate.keySet()){
                errorMap.put(key, validate.get(key));
            }
            return new ApiResponse(false, ExceptionMessage.EMPTY_INFO, errorMap);
        }

        int id = boardVO.getId();

        Optional<BoardVO> isExist = boardService.selectBoardVOById(id);

        if(isExist.isPresent()){
            boardService.updateBoardVO(boardVO);
            return new ApiResponse(true, ExceptionMessage.SAVE_SUCCESS);
        }else{
            throw new BadRequestException(ExceptionMessage.NOT_FOUND_ARTICLE);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse boardDelete(
            @PathVariable int id
    ) throws Exception{
        Optional<BoardVO> boardVO = boardService.selectBoardVOById(id);

        if(boardVO.isPresent()){
            try {
                boardService.deleteById(id);
            }catch (Exception e){
                throw new BadRequestException(ExceptionMessage.DELETE_FAIL);
            }
        }else{
            throw new BadRequestException(ExceptionMessage.NOT_FOUND_ARTICLE);
        }

        return new ApiResponse(true, ExceptionMessage.DELETE_SUCCESS);
    }

//    ajax 파일 저장
    @PostMapping("/ajaxFileUpload")
    public ResponseEntity ajaxFileUpload(
            @RequestParam(value = "file")MultipartFile multipartFile
    ) throws Exception{
        UploadFileVO uploadFileVO = uploadFileService.saveFile(multipartFile);
        String savedFileId = String.valueOf(uploadFileVO.getId());
        return ResponseEntity.ok(new ApiResponse(true, savedFileId));
    }
}
