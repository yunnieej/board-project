package com.study.board.Controller;

import com.study.board.dto.BoardDTO;
import com.study.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// rest api -> 주소상으로 자원을 표현, 식별하는 의미

@Controller
@RequiredArgsConstructor
@RequestMapping("/board") // board로 시작하는 주소는 이 Controller가 받음
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/save")
    public String saveForm() {
        return "save";
    }

    // model은 controller에서 생성된 데이터를 view로 전달
    // ModelAttribute로 boardDTO클래스 객체를 찾아서
    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO) {
        System.out.println("객체 받아옴");
        System.out.println("boardDTO = " + boardDTO);
        boardService.save(boardDTO);
        return "index";
    }

    //전체 목록을 database 로부터 가져와야하기 때문에 model 객체 사용
    @GetMapping("/lists")
    public String findAll(Model model) {
        // DB에서 전체 게시글 데이터를 가져와서 list.html에 보여준다.
        System.out.println("객체 리스트 받아옴");
        List<BoardDTO> boardDTOList = boardService.findAll();
        // 가져온 데이터 model 객체에 담아야 함.
        model.addAttribute("boardList", boardDTOList); //boardDTOList 데이터를 담은 boardList
        return "list";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model) {
        // @PageableDefault(page = 1) Pageable pageable
        /*
            해당 게시글의 조회수를 하나 올리고
            게시글 데이터를 가져와서 detail.html에 출력
            -> controller에서 2가지 method 호출
            id에 맞는 데이터를 database에서 가져와야하기 때문에 model 사용
         */

        System.out.println("상세페이지 출력");
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("board", boardDTO);
//        model.addAttribute("page", pageable.getPageNumber());
        return "detail";
    }


    @GetMapping("/update/{id}")
    public String upDateForm(@PathVariable("id") Long id, Model model) {
        System.out.println("아이디 " + id + "번 수정하기");
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("boardUpdate", boardDTO); //attributeName이 html에서 받을 name
        return "update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute BoardDTO boardDTO, Model model) {
        System.out.println(boardDTO + " 수정 완료");
        BoardDTO board = boardService.update(boardDTO); //수정 반영된 board를 가지고 그냥 detail로 가져가는..
        model.addAttribute("board", board);
        System.out.println("넘어가는 모델 : "+ model);
        return "detail";
//        return "redirect:/board/" + boardDTO.getId();
    }

    @GetMapping("delete/{id}")
    public String delete(@PathVariable Long id) {
        boardService.delete(id);
        return "redirect:/board/";
    }

    /***
     * prev : 이전으로 가는 화살표 표시 여부
     * next : 다음으로 가는 화살표 표시 여부
     * startPage : 시작 페이지 번호
     * currentPage : 현재 페이지 번호
     * endPage : 끝 페이지 번호
     * displayPageNum : 한번에 표시할 페이지 개수
     * totalPosts : 모든 글 개수
     */
    // /board/paging?page=1
    @GetMapping("/paging")
    public String paging(@PageableDefault(page = 1) Pageable pageable, Model model) {
        System.out.println("페이징 처리한 게시판 목록 보기");
        // BoardDTO가 담긴 Page 객체
        System.out.println("==========================");
        System.out.println("pageable= " + pageable);
        Page<BoardDTO> boardList = boardService.paging(pageable);

        int blockLimit = 3;
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1; // 1 4 7 10 ~~
        int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ? startPage + blockLimit - 1 : boardList.getTotalPages();

        // page 갯수 20개
        // 현재 사용자가 3페이지
        // 1 2 3
        // 현재 사용자가 7페이지
        // 7 8 9
        // 보여지는 페이지 갯수 3개
        // 총 페이지 갯수 8개

        model.addAttribute("boardList", boardList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "paging";
    }
}
















