package com.kh.spring.member.controller;

import java.beans.PropertyEditor;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.kh.spring.member.model.service.MemberService;
import com.kh.spring.member.model.vo.Member;

import lombok.extern.slf4j.Slf4j;

/**
 * Model
 * - view단에서 처리할 데이터 저장소. Map 객체
 * 1.Model<<interface>>
 * 		-viewName 리턴
 * 		-addAttribute(k,v)
 * 2.ModelMap
 * 		-viewName 리턴
 * 		-addAttribute(k,v)
 * 3.ModelAndView 
 * 		-ViewName(JSP위치, redirect location) 포함, ModelAndView 객체 리턴
 * 		-addObject(k,v)
 *		-RedirectAttributes객체와 함께 사용하지 말 것.
 *
 *@ModelAttribute
 *1. 메소드레벨
 *	- 해당메소드의  리턴값을 model에 저장해서 모든 요청(핸들러)에 사용.
 *2. 메소드매개변수
 *	- model에 저장된 동일한 이름의 속성(session)이 있는 경우 getter로 사용
 *	- 해당 매개변수를 model 속성으로 저장
 *		-커맨드객체에 @ModelAttribute(속성명)으로 지정
 *		-단순 사용자 입력값은 @RequestParam으로 처리할 것 
 *
 *
 */
@Slf4j
@Controller
@RequestMapping("/member")
@SessionAttributes(value={"loginMember", "anotherValue"}) //여러개는 이렇게 씀 
public class MemberController {
	@Autowired
	private MemberService memberService;
	
	//랜덤 솔트값 -> 매번 다르다.
	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoder;
	
	@ModelAttribute("common")
	public Map<String, Object> common(){
		log.info("@ModelAttribute - common 실행!");
		Map<String, Object> map = new HashMap<>();
		map.put("adminEmail", "admin@spring.kh.com");
		return map;
	}
	
	@GetMapping("/memberEnroll.do")
	public void memberEnroll() {
		//ViewTranslator에 의해서 요청 url에서 view단 jsp주소를 추론한다.	
	}
	
	@PostMapping("/memberEnroll.do")
	public String memberEnroll(Member member, RedirectAttributes attr) {
		log.info("member{}",member);
		try {
			//암호화 처리
			String rawPassword = member.getPassword();
			String encodedPassword = bcryptPasswordEncoder.encode(rawPassword);
			log.info("rawPassword = {}",rawPassword);
			log.info("encodedPassword = {}", encodedPassword);
			member.setPassword(encodedPassword);
			
			memberService.insertMember(member);
			String msg = "회원 등록 성공!";
			attr.addFlashAttribute("msg", msg);
		
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
		return "redirect:/";
	}
	
	/**
	 * 커맨드 객체 이용시 사용자 입력값(String)을 특정 필드타입으로 변환할 editor객체를 설정
	 * 자동으로 호출됨
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		//Member.birthDay:java.sql.Date 타입처리
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		//커스텀 에디터 생성 : allowEmpty - true (빈 문자열 처리 허용) 
		PropertyEditor editor = new CustomDateEditor(sdf, true);
		binder.registerCustomEditor(java.sql.Date.class, editor);
	}
	
	@GetMapping("/login.do")
	public ModelAndView login(ModelAndView mav) {
		// 자동으로 /WEB-INF/views/member/login.jsp를 찾음
		// 현재 요청 url에서 추측함
		mav.addObject("test", "hello world");
		mav.setViewName("member/login");
		return mav;
	}
	
	/**
	 * requestAttr랑 modleview 충돌 검색
	 * 
	 * 충돌나서 HttpServletRequest를 대신쓴다
	 * @param id
	 * @param password
	 * @param mav
	 * @param request
	 * @return
	 */
	
	@PostMapping("/login.do")
	public ModelAndView login(
		   @ModelAttribute @RequestParam String id,
						@RequestParam String password,
						ModelAndView mav,
						HttpServletRequest request) {
		log.info("id={},password={}", id, password);
		//해당 id의 member조회
		Member member = memberService.selectOneMember(id);
		log.info("member = {}", member);
		log.info("encodePassword = {}",bcryptPasswordEncoder.encode(password));
		
		try {
			//로그인 여부처리
			if(member != null &&
					bcryptPasswordEncoder.matches(password,member.getPassword()))
			{
				//로그인 성공
				//기본값으로 request scope 속성에 저장.
				//클래스 레벨에 @SessionAttributes("loginMember")지정하면, session scope에 저장
				mav.addObject("loginMember", member);

			}else {
				//로그인 실패
				//redirectAttr.addFlashAttribute("msg", "아이디 또는 비밀번호가 일치하지 않습니다.");
				FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
				flashMap.put("msg", "아이디 또는 비밀번호가 일치하지 않습니다.");				
			}
			
			mav.setViewName("redirect:/");
			return mav;
			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	
	/**
	 * @SessionAttributes 를 통한 로그인은
	 * SessionStatus객체를 통해서 사용완료처리함으로 로그아웃한다.
	 * @return
	 */
	@GetMapping("/logout.do")
	public String logout(SessionStatus sessionStatus) {
		if(!sessionStatus.isComplete()) {
			sessionStatus.setComplete();
		}
		return "redirect:/";
	}
	
	@GetMapping("/memberDetail.do")
	public ModelAndView memberDetail(@ModelAttribute("loginMember") Member loginMember, ModelAndView mav) {
		log.info("loginMember ={}", loginMember);
		mav.setViewName("member/memberDetail");
		
		return mav;
	}
	
	//수정,삭제는 결과가 0일수도있다.
	@PostMapping("/memberUpdate.do")
	public ModelAndView update(Member member, ModelAndView mav) {
		log.info("update member = {}", member);
		
		try {
			int result = memberService.updateMember(member);
			
			if(result > 0 ) {
				//throw new RuntimeException("멤버가 업슴");
			}
			
			String msg = "수정 성공 🤗	";
			mav.addObject("msg", msg);
			mav.addObject("loginMember", member);
			mav.setViewName("redirect:/member/memberDetail.do");
			
			return mav;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
}
