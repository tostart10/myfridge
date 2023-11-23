package com.icia.web.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.icia.web.model.MyIrdnt;



@Repository("myIrdntDao")
public interface MyIrdntDao
{
	//나만의 냉장고에 저장된 내 재료들 이미지 리스트
	public List<MyIrdnt> myIrdntList(MyIrdnt myIrdnt);
	
	public int myIrdntTotalCount(MyIrdnt myIrdnt);
	
}
