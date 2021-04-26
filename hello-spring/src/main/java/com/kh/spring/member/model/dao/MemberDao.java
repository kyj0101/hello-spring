package com.kh.spring.member.model.dao;

import com.kh.spring.member.model.vo.Member;

public interface MemberDao {

	int insertMember(Member member);

	void login(String id);

	Member selectOneMember(String id);

	int updateMember(Member member);



}
