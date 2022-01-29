package com.example.chapter6.board.controller;

import com.example.chapter6.Util.MediaUtil;
import com.example.chapter6.board.service.BoardService;
import com.example.chapter6.file.FileMapService;
import com.example.chapter6.file.UploadFileService;
import com.example.chapter6.model.*;
import com.example.chapter6.payload.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/board")
public class BoardController {

    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

    private BoardService boardService;
    private UploadFileService uploadFileService;
    private FileMapService fileMapService;

    public BoardController(BoardService boardService, UploadFileService uploadFileService, FileMapService fileMapService) {
        this.boardService = boardService;
        this.uploadFileService = uploadFileService;
        this.fileMapService = fileMapService;
    }

    //    게시물 목록
    @RequestMapping("/list")
    public String boardList(
            @ModelAttribute SearchHelper searchHelper,
            Model model
    ) throws Exception {
        HashMap<String, Object> result = boardService.selectBoardVO(searchHelper);

        logger.info(searchHelper.toString());

        model.addAttribute("searchHelper", result.get("searchHelper"));
        model.addAttribute("result", result.get("list"));

        return "board/list";
    }

    @RequestMapping("/gallery")
    public String boardGallery(
            @ModelAttribute SearchHelper searchHelper,
            Model model
    ) throws Exception{
        HashMap<String, Object> result = boardService.selectBoardVO(searchHelper);

        logger.info(searchHelper.toString());

        List<BoardVO> list = (List<BoardVO>) result.get("list");
        for(BoardVO b : list){
            List<UploadFileVO> fileList = uploadFileService.selectFileByBoardId(b.getId());
            for (UploadFileVO f : fileList) {
                if (f.getContentType().contains("image")) {
                    b.setFileId(f.getId());
                    break;
                }
            }
        }

        model.addAttribute("searchHelper", result.get("searchHelper"));
        model.addAttribute("result", list);

        return "board/gallery";
    }

//    게시물 조회
    @RequestMapping("/view")
    public String boardView(
            @RequestParam(value = "id", defaultValue = "0") int id,
            @ModelAttribute SearchHelper searchHelper,
            HttpServletRequest request,
            Model model
    ) throws Exception {
        if(id > 0){
            // 게시물 조회
            // 게시글 작성자인지 확인하여 수정/삭제 버튼을 생성
            HttpSession session = request.getSession();
            MemberVO sessionResult = (MemberVO) session.getAttribute("memberVO");

            Optional<BoardVO> boardVO = boardService.selectBoardVOById(id);

            if(boardVO.isPresent()){
                model.addAttribute("boardVO", boardVO.get());
                model.addAttribute("searchHelper", searchHelper);
                model.addAttribute("user", sessionResult);
                List<UploadFileVO> fileList = uploadFileService.selectFileByBoardId(boardVO.get().getId());
                model.addAttribute("uploadFileVO", fileList);
            }else{
                model.addAttribute("data", new Message("게시물이 없습니다.", "/board/list"));
                return "message/message";
            }
        }else{
            // 오류 출력 및 목록으로
            model.addAttribute("data", new Message("게시물이 없습니다.", "/board/list"));
            return "message/message";
        }

        return "board/view";
    }

//    글쓰기 페이지로 이동
    @RequestMapping("/write")
    public String boardWrite(
            @ModelAttribute BoardVO boardVO,
            @ModelAttribute SearchHelper searchHelper,
            HttpServletRequest request,
            Model model
    ) throws Exception {
        HttpSession session = request.getSession();
        MemberVO sessionResult = (MemberVO) session.getAttribute("memberVO");

        if(boardVO.getId() == 0) {
            BoardVO newBoardVO = new BoardVO();
            newBoardVO.setCode(searchHelper.getSrchCode());
            newBoardVO.setRegId(sessionResult.getUserId());
            model.addAttribute("boardVO", newBoardVO);
        }else {
            model.addAttribute("boardVO", boardVO);
        }
        model.addAttribute("searchHelper", searchHelper);
        return "board/write";
    }

//    새글 저장 및 수정 저장
    @PostMapping("/save")
    public String boardSave(
            @ModelAttribute BoardVO boardVO,
            @RequestParam(value = "file") List<MultipartFile> multipartFile,
            HttpServletRequest request,
            Model model
    ) throws Exception{
        HttpSession session = request.getSession();
        MemberVO sessionResult = (MemberVO) session.getAttribute("memberVO");

        if(sessionResult != null){
            // 저장 또는 수정
            logger.info("게시글 : " + boardVO.toString());

            if(boardVO.getId() > 0){
                // 수정
                boardService.updateBoardVO(boardVO);
            }else{
                // 저장
                boardService.insertBoardVO(boardVO);
                int insertedId = boardVO.getId();
            }

            for(int i=0; i<multipartFile.size(); i++){
                UploadFileVO uploadFileVO = uploadFileService.saveFile(multipartFile.get(i));
                logger.info("uploadFileVO - {}", uploadFileVO);
                if(uploadFileVO == null){
                    continue;
                }
                FileMapVO fileMapVO = new FileMapVO();
                fileMapVO.setFileId(uploadFileVO.getId());
                fileMapVO.setBoardId(boardVO.getId());
                fileMapService.insertFileMap(fileMapVO);
            }
        }else{
            // 세션 없음 (로그인 안됨)
            model.addAttribute("data", new Message("로그인 후 이용하실 수 있습니다.", "/member/login"));
            return "message/message";
        }

        return "redirect:/board/list";
    }

//    게시물 수정하러 글쓰기 페이지로 이동
    @GetMapping("/modify")
    public String boardModify(
            @RequestParam(value = "id", defaultValue = "0") int id,
            @ModelAttribute SearchHelper searchHelper,
            Model model
    ) throws Exception{
        if(id > 0){
            // 게시물 조회
            Optional<BoardVO> boardVO = boardService.selectBoardVOById(id);

            if(boardVO.isPresent()){
                model.addAttribute("boardVO", boardVO.get());
                model.addAttribute("searchHelper", searchHelper);
                List<UploadFileVO> fileList = uploadFileService.selectFileByBoardId(boardVO.get().getId());
                model.addAttribute("uploadFileVO", fileList);
            }else{
                Message message = new Message();
                message.setMessage("게시물이 없습니다.");
                message.setHref("/board/list?srchCode=" + searchHelper.getSrchCode() + "&srchType=" + searchHelper.getSrchType()
                        + "&srchKeyword=" + searchHelper.getSrchKeyword());
                model.addAttribute("data", new Message());
                return "message/message";
            }
        }else{
            // 오류 출력 및 목록으로
            Message message = new Message();
            message.setMessage("게시물이 없습니다.");
            message.setHref("/board/list?srchCode=" + searchHelper.getSrchCode() + "&srchType=" + searchHelper.getSrchType()
                    + "&srchKeyword=" + searchHelper.getSrchKeyword());
            model.addAttribute("data", new Message());
            return "message/message";
        }

        return "board/write";
    }

//    배열 형태로 게시물 삭제
    @PostMapping("/delete")
    public String delete(
            @RequestParam(value = "del[]", defaultValue = "") int[] del,
            Model model
    ) throws Exception{
        logger.info("삭제 배열 - {}", del);

        Message message = new Message();
        message.setHref("/board/list");

        if(del.length > 0){
            for(int i=0; i<del.length; i++){
                boardService.deleteById(del[i]);
            }
            message.setMessage("삭제되었습니다.");
        }else{
            message.setMessage("삭제할 게시물을 선택하세요.");
        }

        model.addAttribute("data", message);
        return "message/message";
    }

    @PostMapping("/saveTest")
    public String boardSaveTest(
            @RequestParam(value = "file") MultipartFile multipartFile
    ) throws Exception{

        logger.info(multipartFile.getName());
        logger.info(multipartFile.getContentType());
        logger.info(multipartFile.getOriginalFilename());
        logger.info(String.valueOf(multipartFile.getSize()));

        Path path = Paths.get("C:/Users/USER/Desktop/Spring config/upload_file/").toAbsolutePath().normalize();
        Files.createDirectories(path);

        logger.info("선택한 파일 명 - {}", multipartFile.getOriginalFilename());

        String generateFileName = UUID.randomUUID().toString();

        int dot = multipartFile.getOriginalFilename().lastIndexOf(".");

        if(dot != -1 && multipartFile.getOriginalFilename().length() - 1 > dot){
            String extention = multipartFile.getOriginalFilename().substring(dot+1);
            generateFileName += "." + extention;
        }
        logger.info("변경된 파일 명 - {}", generateFileName);

        File file = new File("C:/Users/USER/Desktop/Spring config/upload_file/" + generateFileName);

        multipartFile.transferTo(file);

        return "test";
    }

    @GetMapping("/file/download")
    @ResponseBody
    public ResponseEntity fileDownload(
            @RequestParam(value = "name", defaultValue = "") String name
    ) throws UnsupportedEncodingException, MalformedURLException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + new String(name.getBytes("UTF-8"), "ISO-8859-1") + "\"");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        Path path = Paths.get("C:/Users/USER/Desktop/Spring config/upload_file/" + name).toAbsolutePath().normalize();
        logger.info(String.valueOf(path));
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @GetMapping("/file/{fileId}")
    @ResponseBody
    public ResponseEntity showFile(
        @PathVariable int fileId
    ) throws Exception {
        UploadFileVO uploadFileVO = uploadFileService.selectFileById(fileId);

        if(uploadFileVO == null)
            return ResponseEntity.badRequest().build();

        String fileName = uploadFileVO.getFileName();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");

        if(MediaUtil.containsMediaType(uploadFileVO.getContentType())){
            headers.setContentType(MediaType.valueOf(uploadFileVO.getContentType()));
        }else{
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }

        Resource resource = uploadFileService.loadAsResource(uploadFileVO.getSaveFileName());

        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @GetMapping("/deleteFile/{fileId}")
    @ResponseBody
    public ApiResponse deleteFile(
            @PathVariable int fileId
    ){
        UploadFileVO uploadFileVO = uploadFileService.selectFileById(fileId);
        if(uploadFileVO != null){
            File deleteFile = new File(uploadFileVO.getFilePath());
            if(deleteFile.exists())
                deleteFile.delete();
            uploadFileService.deleteFileById(fileId);

            return new ApiResponse(true, "삭제 완료");
        }else{
            return new ApiResponse(false, "삭제 오류");
        }
    }
}
