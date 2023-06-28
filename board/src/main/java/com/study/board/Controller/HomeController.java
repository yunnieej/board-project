package com.study.board.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // 빈으로 등록
public class HomeController {
    @GetMapping("/") // 시작 주소를 받아주는 index method -> index html 띄워줌
    public String index(){
        return "index";
    }
}
