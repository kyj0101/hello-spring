package com.kh.spring.demo.controller;

import java.net.http.HttpRequest;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.demo.model.service.DemoService;
import com.kh.spring.demo.model.vo.Dev;

import lombok.extern.slf4j.Slf4j;

/*
* 컨트롤러클래스 메소드가 가질수 있는 파라미터
* HttpServletRequest
* HttpServletResponse
* HttpSession
* java.util.Locale : 요청에 대한 Locale
* InputStream/Reader : 요청에 대한 입력스트림
* OutputStream/Writer : 응답에 대한 출력스트림. ServletOutputStream, PrintWriter
						
* @PathVariable: 요청url중 일부를 매개변수로 취할 수 있다.
* @RequestParam
* @RequestHeader
* @CookieValue
* @RequestBody

뷰에 전달할 모델 데이터 설정
* ModelAndView
* Model
* ModelMap 
* @ModelAttribute : model속성에 대한 getter
* @SessionAttribute : session속성에 대한 getter
* SessionStatus: @SessionAttribute로 등록된 속성에 대하여 사용완료(complete)처리
* Command객체 : http요청 파라미터를 커맨드객체에 저장한 VO객체
* Error, BindingResult : Command객체에 저장결과, Command객체 바로 다음위치시킬것.

기타
* MultipartFile : 업로드파일 처리 인터페이스. CommonsMultipartFile
* RedirectAttributes : DML처리후 요청주소 변경을 위한 redirect를 지원
* 
*/











//@Slf4j logging
//@Component의 기능을 포함함.
@Slf4j
@Controller
@RequestMapping("/demo")
public class DemoController {
	
	//@Slf4j에 의해 생성되는 코드 
	//Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private DemoService demoService;
	
	/**
	 * @RequestMapping
	 * 사용자 요청 url에 따라 호출되는 메소드
	 *
	 */
	@RequestMapping("/devForm.do")
	public String devForm() {
		return "demo/devForm";
	}
	
	//@RequestMapping("/demo/dev1.do")//GET,POST 처리
	@RequestMapping(value="/dev1.do", method=RequestMethod.POST)//GET만 처리
	public String dev1(HttpServletRequest request, HttpServletResponse response, Model model) {
		String name = request.getParameter("name");
		int career = Integer.valueOf(request.getParameter("career"));
		String email = request.getParameter("email");
		String gender = request.getParameter("gender");
		String[] lang = request.getParameterValues("lang");
		
		Dev dev = new Dev(0, name, career, email, gender, lang);
		//System.out.println(dev);
		//log.debug("dev = {}, {}", dev, name); //중괄호 안에 파라미터를 넣어줌. 개수 제한은 없다.
		log.info("dev = {}", dev);
		
		//request.setAttribute("dev", dev);
		model.addAttribute("dev",dev); //model객체를 통해 jsp에 객체,값 전달
		
		return "demo/devResult";
	}
	
	@RequestMapping(value="/dev2.do", method=RequestMethod.POST)
	public String dev2(
				@RequestParam(value="name")String namee,
				@RequestParam(value="career")int career,
				@RequestParam(value="email")String email,
				@RequestParam(value="gender", defaultValue= "M")String gender,
				@RequestParam(value="lang",required=false)String[] lang, 
				Model model
				) //@RequestParam name값과 일치해야지 정상적으로 값이 넘어옴 형변환도 해줌
				  //defaultValue : 값 안넘어올때 기본값
				 //required=false : 값 안넘어와도 에러안남
	
	{
		Dev dev = new Dev(0,  namee, career, email, gender, lang);
		log.info("{}", dev);
		model.addAttribute("dev", dev);
		return "demo/devResult";
	}
	
	/**
	 * 커맨드 객체 : 사용자 입력값과 일치하는 필드에 값대입
	 * 커맨드 객체는 자동으로 model속성으로 지정된다.
	 * 
	 * 
	 */
	//@RequestMapping(value="/dev3.do", method=RequestMethod.POST)
	@PostMapping("/dev3.do")//@RequestMapping과 같은 일을 함 대신 POST만 처리 (GET도 따로 어노테이션 있음)
	public String dev3(@ModelAttribute("dddev") Dev dev) {
		log.info("{}", dev);
		return "demo/devResult";
	}
	
	@PostMapping("/insertDev.do")
	public String insertDev(Dev dev, RedirectAttributes redirectAttr) {
		log.info("{}", dev);

		//1. 업무로직
		int result = demoService.insertDev(dev);
		//2. 사용자 피드백 및 리다이렉트(DML)
		String msg = result > 0 ? "Dev 등록성공!" : "Dev 등록실패!";
		log.info("처리결과 : {}", msg);
		
		//리다이렉트 후 사용자 피드백 전달하기
		redirectAttr.addFlashAttribute("msg", msg);
		return "redirect:/";
	}
	
	@GetMapping("/devList.do")
	public String devList(Model model) {
		//1. 업무로직
		List<Dev> list = demoService.selectDevList();
		log.info("list = {}", list);
		
		//2.jsp위임
		model.addAttribute("list", list);

		return "demo/devList";
	}
	
	@GetMapping("/updateDev.do")
	public String devUpdate(int no, Model model) {
		log.info("no = {}" + no);
		
		Dev dev = demoService.selectDevOne(no);
		model.addAttribute("dev", dev);
		log.info("{}",dev);

		return "demo/devUpdateForm";
	}
	
	@PostMapping("/updateDev.do")
	public String devUpdatePost(Dev dev, RedirectAttributes redirectAttr) {
		log.info("{}", dev);
		int result = demoService.updateDev(dev);
		String msg = result > 0 ? "성공" : "실패";
		redirectAttr.addFlashAttribute("msg", msg);
		log.info("메시지{}" , msg);
		
		return "redirect:/demo/devList.do";
	}
	
	@PostMapping("/deleteDev.do") //@RequestParam -> 파라미터를 필수로 지정
	public String devDelete(@RequestParam int no, RedirectAttributes redirectAttr) {
		log.info("delete {}",no);
		int result = demoService.deleteDev(no);
		String msg = result > 0 ? "성공" : "실패";
		redirectAttr.addFlashAttribute("msg", msg);
		log.info("메시지{}", msg);
		
		return "redirect:/demo/devList.do";
	}
}
