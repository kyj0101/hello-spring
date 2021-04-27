package com.kh.spring.board.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.vo.Attachment;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.common.HelloSpringUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequestMapping("/board")
public class BoardController {
	@Autowired
	private BoardService boardService;
	
	/**
	 * form[enctype=multipart/form-data] -> cos.jar
	 * 
	 * handler - @RequestParam MultipartFile upFile -> apache
	 * 1. 서버 컴퓨터에 파일저장 : 
	 * 	-saveDirectory(/resources/upload/board/20210427_234.jpg)
	 * 	-파일명재지정
	 * 2. DB에 attachement에 저장된 파일정보
	 * 	-attachement저장
	 * 
	 * @param cPage
	 * @param model
	 * @param request
	 */
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
	public String boardForm(Board board, 
							@RequestParam(value="upFile", required = false)MultipartFile[] upFiles,
							HttpServletRequest request,
							RedirectAttributes attr) {
	
		try {
			log.info("boardForm parameter board = {}", board);
			//0. 파일저장 및 Attachment객체 생성
			//저장경로
			String saveDirectory = request.getServletContext().getRealPath("/resources/upload/board");
			
			//File 객체를 통해서 directory기능
			//진짜 File X file이 있을수도 없을수도 있는 file을 가리키는(디렉토리를 바라보는)자바객체
			File dir = new File(saveDirectory);
			if(!dir.exists()) {
				dir.mkdirs(); //복수개 폴더 생성 가능
			}
			
			//복수개의 Attachement객체를 담을 list생성
			List<Attachment> attachList = new ArrayList<Attachment>();
			
			for(MultipartFile upFile : upFiles) {
				if(upFile.isEmpty()) {
					continue;
				}
				
				log.info("upFile = {}", upFile);
				log.info("upFile.name = {}", upFile.getOriginalFilename());
				log.info("upFile.size = {}", upFile.getSize());
				
				//저장할 파일명 생성
				File renameFile = HelloSpringUtils.getRenamedFile(saveDirectory, upFile.getOriginalFilename());
				upFile.transferTo(renameFile);
				
				//Attachment객체 생성
				Attachment attach = new Attachment();
				attach.setOriginalFileName(upFile.getOriginalFilename());
				attach.setRenamedFileName(renameFile.getName());
				attachList.add(attach);
			}
			if(upFiles.length > 0) {
		
				
			}
			
			board.setAttachList(attachList);
			int result = boardService.insertBoard(board);
			log.info("boardForm insertBoard result = {}", result > 0 ? "성공" : "실패");
			
			return "redirect:/board/boardList.do";
		}catch (IOException | IllegalStateException e) {
			log.error("첨부파일 저장 오류!", e);
			throw new BoardException("첨부파일 저장 오류!");
		}
		catch (Exception e) {
			throw e;
		}
	}
	
//	@실습문제 : 게시글 상세보기
//	- 첨부파일 개수만큼 버튼생성
//	- 조인query 사용하지 말고, board, attachment 테이블 각각 조회query 작성할 것.
	@GetMapping("/boardDetail.do")
	public Model boardDetail(int no, Model model) {
		try {			
			Board board = boardService.selectOneBoard(no);
			model.addAttribute("board", board);
			log.info("boardDetail board = {}", board);
			
			return model;
		} catch (RuntimeException e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
}
