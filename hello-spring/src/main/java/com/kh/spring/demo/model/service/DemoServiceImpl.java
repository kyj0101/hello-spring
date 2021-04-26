package com.kh.spring.demo.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.spring.demo.model.dao.DemoDao;
import com.kh.spring.demo.model.vo.Dev;

@Service
public class DemoServiceImpl implements DemoService {
	@Autowired
	private DemoDao demoDao;

	@Override
	public int insertDev(Dev dev) {
		//1.SqlSession객체 생성 -> aop
		//2.dao 업무요청
		int result = demoDao.insertDev(dev);
		//3.트랜잭션 처리 ->  aop 
		//4.SqlSession 반납 -> aop
		return result;
	}

	@Override
	public List<Dev> selectDevList() {
		//1.SqlSession객체 생성 -> aop
		//2.dao 업무요청
		//3.트랜잭션 처리 ->  aop 
		//4.SqlSession 반납 -> aop
		return demoDao.selectDevList();
	}

	@Override
	public Dev selectDevOne(int no) {
		return demoDao.selectDevOne(no);
	}

	@Override
	public int updateDev(Dev dev) {
		return demoDao.updateDev(dev);
	}

	@Override
	public int deleteDev(int no) {
		return demoDao.deleteDev(no);
	}
}
