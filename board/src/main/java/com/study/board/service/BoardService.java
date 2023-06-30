package com.study.board.service;

import com.study.board.dto.BoardDTO;
import com.study.board.entity.BoardEntity;
import com.study.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// DTO -> Entity로 변환(entity class)
// Entity -> DTO(dto class)
@Service
@RequiredArgsConstructor
public class BoardService {
    // repository는 기본적으로 entity 클래스만 받아줌
    private final BoardRepository boardRepository;
    public void save(BoardDTO boardDTO){
        BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
        boardRepository.save(boardEntity);
    }

    public List<BoardDTO> findAll(){
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        // repositoy -> service로 넘어오면 대부분 entity
        // entity로 넘어온 객체를 dto 객체로 옮겨담아서 컨트롤러로 return
        List<BoardDTO> boardDTOList = new ArrayList<>();
        for (BoardEntity boardEntity: boardEntityList){
            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
        }
        return boardDTOList; // controller로 리턴
    }

    @Transactional
    public void updateHits(Long id) {
        boardRepository.updateHits(id);
    }

    public BoardDTO findById(Long id){
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);

        if (optionalBoardEntity.isPresent()){
            BoardEntity boardEntity = optionalBoardEntity.get();
            BoardDTO boardDTO = BoardDTO.toBoardDTO(boardEntity);
            return boardDTO;
        }
        else{
            return null;
        }
    }

    // spring jpa는 save method로 update, insert 다 가능 -> 구분은 id값이 있는지 없는지로 함
    public BoardDTO update(BoardDTO boardDTO){
        BoardEntity boardEntity = BoardEntity.toUpdateEntity(boardDTO);
        boardRepository.save(boardEntity);
        return findById(boardDTO.getId()); // 위메서드에서 그냥 가져온 것
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    // DB로부터 페이징 처리된 데이터를 가져오는 것
    public Page<BoardDTO> paging(Pageable pageable) {
        // page 위치에 있는 값은 0부터 시작함 -> 실제 사용자가 요청한 값에서 하나 빠진 값이 요청되야함.
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 3; // 한 페이지에 보여질 글 개수
        // 한 페이지당 3개씩 글을 보여준 후 정렬 기준은 id 기준으로 내림차순 정렬
        Page<BoardEntity> boardEntities = boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        // Page entity가 제공하는 method
        System.out.println("boardEntities.getContent() = " + boardEntities.getContent()); // 요청 페이지에 해당하는 글
        System.out.println("boardEntities.getTotalElements() = " + boardEntities.getTotalElements()); // 전체 글갯수
        System.out.println("boardEntities.getNumber() = " + boardEntities.getNumber()); // DB로 요청한 페이지 번호
        System.out.println("boardEntities.getTotalPages() = " + boardEntities.getTotalPages()); // 전체 페이지 갯수
        System.out.println("boardEntities.getSize() = " + boardEntities.getSize()); // 한 페이지에 보여지는 글 갯수
        System.out.println("boardEntities.hasPrevious() = " + boardEntities.hasPrevious()); // 이전 페이지 존재 여부
        System.out.println("boardEntities.isFirst() = " + boardEntities.isFirst()); // 첫 페이지 여부
        System.out.println("boardEntities.isLast() = " + boardEntities.isLast()); // 마지막 페이지 여부

        //paging 화면목록 : id, writer, title, hits, createdTime
        Page<BoardDTO> boardDTOS = boardEntities.map(board -> new BoardDTO(board.getId(), board.getBoardWriter(), board.getBoardTitle(), board.getBoardHits(), board.getCreatedTime()));

        return boardDTOS;
    }
}

/***
 * MVC 패턴 - 1
 * 고객 요청옴 -> Controller에서 비즈니스 로직 수행
 * 로직 수행 후 -> Model에 데이터 담음
 * 그 다음 view 로직으로 제어권을 넘김
 * 그럼 뷰 로직이 model에 있는 데이터 참고 후 뷰를 그려줌
 */
/***
 * MVC 패턴 -2
 * 비즈니스 로직 -> 회원 저장, 주문 같은 것들..
 * Service에 비즈니스 로직들이 작성됨
 * 컨트롤러는 parameter 꺼내고 고객의 http 요청의 스펙 확인 -> 잘못되면  400 오류
 * 잘 되면 service, repository 호출해서 data 저장이나 주문..
 * 결과 받고 (ex. 조회한 회원 목록) model에 전달
 */
/***
 * Model은 HttpServletRequest 객체 사용
 * request.setAttribute(), request.getAttribute()
 * 데이터 보관, 조회 가능
 */
