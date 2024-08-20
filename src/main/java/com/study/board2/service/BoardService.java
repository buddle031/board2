package com.study.board2.service;

import com.study.board2.entity.Board2;
import com.study.board2.repository.BoardRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    //글 작성 처리
    public void write(Board2 board2, MultipartFile file) throws Exception{
        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";
        UUID uuid = UUID.randomUUID(); //랜덤식별자
        String filename = uuid + "_" + file.getOriginalFilename();
        File saveFile = new File(projectPath, filename);
        file.transferTo(saveFile);

        board2.setFilename(filename);
        board2.setFilepath("/files/" + filename);
        boardRepository.save(board2);
    }

    //게시글 리스트 처리
    //리턴값: 매개변수없으면 list. 있으면 page
    public Page<Board2> boardList(Pageable pageable){
        return boardRepository.findAll(pageable);
    }

    public Page<Board2> boardSearchList(String searchKeyword, Pageable pageable){
        return boardRepository.findByTitleContaining(searchKeyword, pageable);
    }

    //특정 게시글 불러오기
    public Board2 boardView(Integer id){
        return boardRepository.findById(id).get();
    }

    //특정 게시물 삭제
    @Transactional
    public void boardDelete(Integer id){
        boardRepository.deleteById(id);
    }
}
