package com.icia.web.model;

import java.io.Serializable;

public class MyIrdnt implements Serializable
{
	//나만의 냉장고에서 저장된 내가 가진 재료들 사용하기 위한 model
	
	private static final long serialVersionUID = 1L;
	
	private String userId;			//사용자 아이디
	private String irdntName;		//재료이름
	private String irdntImage;		//재료 이미지
	
	
	
	//sql문에 넣을려고 작성
	private long startRow;				//시작 rownum
	private long endRow;				//끝 rownum
	private String searchType;
	private String searchValue;
	
	
	//생성자
	public MyIrdnt()
	{
		userId = "";
		irdntName = "";
		irdntImage = "";
		
		startRow = 0;
		endRow = 0;
		searchType = "";
		searchValue = "";
		
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getIrdntName() {
		return irdntName;
	}


	public void setIrdntName(String irdntName) {
		this.irdntName = irdntName;
	}


	public String getIrdntImage() {
		return irdntImage;
	}


	public void setIrdntImage(String irdntImage) {
		this.irdntImage = irdntImage;
	}


	public long getStartRow() {
		return startRow;
	}


	public void setStartRow(long startRow) {
		this.startRow = startRow;
	}


	public long getEndRow() {
		return endRow;
	}


	public void setEndRow(long endRow) {
		this.endRow = endRow;
	}


	public String getSearchType() {
		return searchType;
	}


	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}


	public String getSearchValue() {
		return searchValue;
	}


	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
	
	
	
	
	
	
}
