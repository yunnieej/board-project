package com.study.board.entity;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseEntity {
    @CreationTimestamp //생성시간
    @Column(updatable = false) //수정 시 관여안함
    private LocalDateTime createdTime;

    @UpdateTimestamp //업데이트시간
    @Column(insertable = false) //입력 시 관여안함
    private LocalDateTime updatedTime;
}
