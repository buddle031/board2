package com.study.board2.controller;

import com.study.board2.entity.Board2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.study.board2.service.BoardService;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/board/write")
    public String boardWriteForm(){
        return "boardwrite";
    }

    @PostMapping("/board/writepro")
    public String boardWritePro(Board2 board2, Model model, @RequestParam("file") MultipartFile file) throws Exception{
        boardService.write(board2, file);
        //if(return == 1){
            model.addAttribute("message", "글 작성이 완료되었습니다");
         //} else{
            //model.addAttribute("message", "글 작성이 실패하였습니다");
         //}
        model.addAttribute("searchUrl", "/board/list");
        return "message";
    }

    @GetMapping("/board/list")
    public String boardList(Model model,
                            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            @RequestParam(value = "searchKeyword", required = false) String searchKeyword){
        System.out.println("Search Keyword: " + searchKeyword);

        Page<Board2> list = null;
        if(searchKeyword == null){
            list = boardService.boardList(pageable);
        } else{
            list = boardService.boardSearchList(searchKeyword, pageable);
        }

        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "boardlist";
    }

    @GetMapping("/board/view")
    public String boardView(Model model, @RequestParam("id") Integer id) {
        Board2 board = boardService.boardView(id);
        model.addAttribute("board", board);
        return "boardview";
    }
    /*public String boardView(Model model, Integer id){
        model.addAttribute("board", boardService.boardView(id));
        return "boardview";     //에러원인: null, 잘못된 타입의 객체를 반환할 경우에 에러나
    }*/

    @GetMapping("/board/delete")
    public String boardDelete(@RequestParam("id") Integer id) {
        try {
            boardService.boardDelete(id);
            return "redirect:/board/list";
        } catch (Exception e) {
            e.printStackTrace(); // 예외를 콘솔에 출력합니다.
            return "error"; // 오류 페이지로 리다이렉트합니다.
        }
    }

    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id,
                              Model model){
        model.addAttribute("board", boardService.boardView(id));
        return "boardmodify";
    }

    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board2 board2,
                              Model model, MultipartFile file) throws Exception{
        Board2 boardTemp = boardService.boardView(id);
        boardTemp.setTitle(board2.getTitle());
        boardTemp.setContent(board2.getContent());
        boardService.write(boardTemp, file);

        model.addAttribute("message", "글 수정이 완료되었습니다");
        model.addAttribute("searchUrl", "/board/list");

        return "message";
    }
}
