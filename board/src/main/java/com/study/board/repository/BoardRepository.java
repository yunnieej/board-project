package com.study.board.repository;

import com.study.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

    //update board_table set board_hits = board_hits +1 where id=?
    // value는 entity를 기준으로 query 작성

    //update나 delete는 @Modifying4
    @Modifying
    @Query(value = "update BoardEntity b set b.boardHits = b.boardHits+1 where b.id =:id")
    void updateHits(@Param("id") Long id);

}
