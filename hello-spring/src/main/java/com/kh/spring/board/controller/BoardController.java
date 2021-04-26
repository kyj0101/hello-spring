package com.kh.spring.board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.common.HelloSpringUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequestMapping("/board")
public class BoardController {
	@Autowired
	private BoardService boardService;
	

	@GetMapping("/boardList.do")
	public void boardList(@RequestParam(defaultValue = "1") int cPage, 
						  Model model, 
						  HttpServletRequest request) {
		//1. 사용자 입력값
		int numPerPage = 5;
		log.debug("cPage = {}",cPage);
		Map<String, Object> param = new HashMap<>();
		param.put("numPerPage", numPerPage);
		param.put("cPage", cPage);
		
		//2. 업무로직
		//a. contents 영역 : mybatis의 RowBounds
		List<Board> list = boardService.selectBoardList(param);
		model.addAttribute("list", list);
		log.debug("list = {}", list);
		
		//b. pagebar 영역
		int totalContents = boardService.getTotalContnets();
		String url = request.getRequestURI();
		String pageBar = HelloSpringUtils.getPageBar(totalContents, cPage, numPerPage, url);
		
		//log.debug("totalContents = {}", totalContents);
		//log.debug("pageBar = {}", pageBar);
		
		
		//3. jsp처리 위임
		model.addAttribute("list", list);
		model.addAttribute("pageBar", pageBar);
	}
	
	@GetMapping("/boardForm.do")
	public void boardForm() {}
	
	@PostMapping("/boardEnroll.do")
	public String boardForm(Board board) {
		log.info("boardForm parameter board = {}", board);
		
		try {
			int result = boardService.insertBoard(board);
			log.info("boardForm insertBoard result = {}", result > 0 ? "성공" : "실패");
			
			return "redirect:/board/boardList.do";
		} catch (Exception e) {
			throw e;
		}
	}
	
}
