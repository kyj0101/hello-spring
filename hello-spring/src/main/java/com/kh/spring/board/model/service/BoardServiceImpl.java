package com.kh.spring.board.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.spring.board.model.dao.BoardDao;
import com.kh.spring.board.model.vo.Attachment;
import com.kh.spring.board.model.vo.Board;

import jdk.internal.org.jline.utils.Log;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BoardServiceImpl implements BoardService {
	@Autowired
	private BoardDao boardDao;

	@Override
	public List<Board> selectBoardList(Map<String, Object> param) {
		return boardDao.selectBoardList(param);
	}

	@Override
	public int getTotalContnets() {
		return boardDao.getTotalContents();
	}
	
	/**
	 * @Transactional(rollbackFor = Exception.class)
	 * 이 어노테이션 추가하면 트랜잭션에서 에러나면 
	 * 알아서 rollback됨 
	 * 원래는 board insert하고 attach insert에서 
	 * 에러나면 board는 추가됐는데 
	 * 이번에는 attach에서 에러나면 
	 * board도 rollback같이된다는뜻
	 */
	
	/**
	 * @Transactional
	 * - class level  : 모든 메소드 실행결과 RunTime예외가 던져지면 rollback한다.(예외 없으면 commit)
	 * - method level : 해당 메소드 실행결과 RunTime예외가 던져지면 rollback한다.(예외 없으면 commit)
	 */
					//예외처리
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int insertBoard(Board board) {
		int result = 0;
		
		//1. board객체등록
		result = boardDao.insertBoard(board);
		log.debug("board.no = {}", board.getNo());
		
		//2. attachment 객체 등록
		//insert into attachment(no, board_no, original_filename, rename_filename)
		//value(seq_attachment_no.nextval, #{boardNo}, #{originalFileName}, #{renamedFileName}
		if(!board.getAttachList().isEmpty()) {
			for(Attachment attach : board.getAttachList()) {
				attach.setBoardNo(board.getNo());
				result = boardDao.insertAttachment(attach);
			}
		}
		return result;
	}

	@Override
	public Board selectOneBoard(int no) {
		Board board = boardDao.selectOneBoard(no);
		List<Attachment> attachList = boardDao.selectAttachList(no);
		log.info("attachList =  {}",attachList);
		
		if(attachList.size() > 0) {
			board.setAttachList(attachList);
		}
		return board;
	}
}
