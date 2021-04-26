package com.kh.spring.common.aop;

import java.util.Calendar;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;
@Component
@Aspect
@Slf4j
public class StopwatchAspect {
	//@Pointcut("execution(* com.kh.spring.memo.controller.MemoController.insertMemo(..))")
	@Pointcut("execution(* com.kh.spring.memo.controller..insert*(..))")
	public void pointcut() {}


	@Around("pointcut()")
	public Object aroudAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
		String methodName = joinPoint.getSignature().getName();
		
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		log.info("start stopwatch");
		
		Object obj = joinPoint.proceed();
		
		stopwatch.stop();
		double second = stopwatch.getTotalTimeMillis();
		log.info("{}, stop stopwatch = {}",methodName,second);
		
		return obj;
	}
}
