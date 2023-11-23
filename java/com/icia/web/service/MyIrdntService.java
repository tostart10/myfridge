package com.icia.web.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.icia.web.dao.MyIrdntDao;
import com.icia.web.model.MyIrdnt;

@Service("myIrdntService")
public class MyIrdntService
{
	private static Logger logger = LoggerFactory.getLogger(MyIrdntService.class);
	
	
	//파일 저장 경로
 	@Value("#{env['upload.save.dir']}")
 	private String UPLOAD_SAVE_DIR;
	
 	@Autowired
 	private MyIrdntDao myIrdntDao;
 	
 	//나만의 냉장고 속 나의 재료들 이미지 리스트
 	public List<MyIrdnt> myIrdntList(MyIrdnt myIrdnt)
 	{
 		List<MyIrdnt> list = null;

 		try
		   {
			   list =  myIrdntDao.myIrdntList(myIrdnt);
		   }
		   catch(Exception e)
		   {
			   logger.error("[MyIrdntService] myIrdntList Exception", e);
		   }
 		
 		return list;
 	}
 	
 	public int myIrdntTotalCount(MyIrdnt myIrdnt)
 	{
 		int count = 0;
 		
 		try
 		{
 			count = myIrdntDao.myIrdntTotalCount(myIrdnt);
 		}
 		catch(Exception e)
 		{
 			logger.error("[MyIrdntService] myIrdntTotalCount Exception", e);
 		}
 		
 		return count;
 	}
	
	
}
