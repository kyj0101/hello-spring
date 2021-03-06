package com.kh.spring.demo.model.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.kh.spring.demo.model.vo.Dev;


public interface DemoDao {

	int insertDev(Dev dev);

	List<Dev> selectDevList();

	Dev selectDevOne(int no);

	int updateDev(Dev dev);

	int deleteDev(int no);

}
