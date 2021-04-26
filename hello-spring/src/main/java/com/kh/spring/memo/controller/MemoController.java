package com.kh.spring.memo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.memo.model.service.MemoService;
import com.kh.spring.memo.model.vo.Memo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequestMapping("/memo")
public class MemoController {
	@Autowired
	private MemoService memoService;
	
	/**
	 * AOP의 실행구조
	 * MemoController.memo --------> MemberService.selectMemoList
	 * 
	 * MemoController.memo ----- Proxy객체 ---> Target객체(MemberService.selectMemoList)
	 */
	
	@GetMapping("/memo.do")
	public Model getMemo(Model model) {
		//proxy확인
		log.debug("proxy = {}", memoService.getClass());
		
		
		List<Memo> memoList = memoService.selectMemoList();
		log.info("{}",memoList);
		model.addAttribute("memoList", memoList);
		
		return model;
	}
	
	@PostMapping("/insertMemo.do")
	public String insertMemo(@ModelAttribute Memo memo, RedirectAttributes redirecAttr) {
		try {
			log.info("{}",memo);
			int result = memoService.insertMemo(memo);
			String msg = result > 0 ? "성공" : "실패";
			redirecAttr.addFlashAttribute("msg", msg);

			return "redirect:/memo/memo.do";
			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}
}
