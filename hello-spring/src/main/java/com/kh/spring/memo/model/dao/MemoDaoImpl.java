package com.kh.spring.memo.model.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.spring.memo.model.vo.Memo;

@Repository
public class MemoDaoImpl implements MemoDao{
	
	@Autowired
	SqlSession session;

	@Override
	public List<Memo> selectMemoList() {
		return session.selectList("memo.selectMemoList");
	}

	@Override
	public int insertMemo(Memo memo) {
		return session.insert("memo.insertMemo", memo);
	}

}
