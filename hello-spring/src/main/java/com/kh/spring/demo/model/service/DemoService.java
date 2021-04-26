package com.kh.spring.demo.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kh.spring.demo.model.vo.Dev;


public interface DemoService {

	int insertDev(Dev dev);

	List<Dev> selectDevList();

	Dev selectDevOne(int no);

	int updateDev(Dev dev);

	int deleteDev(int no);

}
