package com.icia.web.controller;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.icia.common.model.FileData;
import com.icia.common.util.FileUtil;
import com.icia.common.util.StringUtil;
import com.icia.web.model.Response;
import com.icia.web.model.User;
import com.icia.web.dao.RecipeInfoDao;
import com.icia.web.dao.RecipeSaveDao;
import com.icia.web.model.BookMark;
import com.icia.web.model.Paging;
import com.icia.web.model.RecipeCooking;
import com.icia.web.model.RecipeInfo;
import com.icia.web.model.RecipeIrdnt;
import com.icia.web.model.RecipeRcm;
import com.icia.web.model.RecipeReply;
import com.icia.web.service.UserService;
import com.icia.web.service.RecipeService;
import com.icia.web.util.CookieUtil;
import com.icia.web.util.HttpUtil;

@Controller("recipeController")
public class RecipeController 
{
   private static Logger logger = LoggerFactory.getLogger(RecipeController.class);
   
   @Autowired
   private RecipeSaveDao recipeSaveDao;
   
   @Autowired
   private RecipeInfoDao recipeInfoDao;
   
   //쿠키명
   @Value("#{env['auth.cookie.name']}")         
   private String AUTH_COOKIE_NAME;            
   
   //파일 저장 경로
   @Value("#{env['upload.save.dir']}")
   private String UPLOAD_SAVE_DIR;
   
   @Autowired
   private RecipeService recipeService;
   
   @Autowired
   private UserService userService;
   
   private static final int LIST_COUNT = 10;
   
   //댓글 리스트용
   private static final int LIST_COUNT2 = 5;
   
   private static final int PAGE_COUNT = 5;      
   
   //레시피 리스트(조회순)
   @RequestMapping(value="/recipe/bestRecipe")         
   public String list(ModelMap model, HttpServletRequest request, HttpServletResponse response)
   {
      //현재 페이지
      long curPage = HttpUtil.get(request, "curPage", (long)1);
      //게시물 리스트
      List<RecipeInfo> list = null;
      //조회 객체
      RecipeInfo search = new RecipeInfo();
      
      //총 게시물 수
      long totalCount = 0;
      //페이징 객체
      Paging paging = null;
      
      totalCount = recipeService.recipeListCount(search);
      //totalCount 디버깅용
      logger.debug("===============================");
      logger.debug("totalCount : " + totalCount);
      logger.debug("===============================");
      
      //BoardList호출
      if(totalCount > 0)
      {
         //페이징 객체 생성   
         paging = new Paging("/recipe/bestRecipe", totalCount, LIST_COUNT, PAGE_COUNT, curPage, "curPage");
         
         search.setStartRow(paging.getStartRow());
         search.setEndRow(paging.getEndRow());
         
         list = recipeService.recipeList(search);
      }
      
      //jsp에 사용할 모델맵에 담아줌
      model.addAttribute("list", list);
      model.addAttribute("curPage", curPage);
      //모델맵에서 페이징 처리를 해줌
      model.addAttribute("paging",paging);
      
      return "/recipe/bestRecipe";
   }
   
   
      //레시피 리스트(음식분류코드)
      @RequestMapping(value="/recipe/recipeSearch")            
      public String nationList(ModelMap model, HttpServletRequest request, HttpServletResponse response)
      {
         //조회항목(1:작성자, 2:제목, 3:내용)
         String searchType = HttpUtil.get(request, "searchType", "");
         //조회값
         String searchValue = HttpUtil.get(request, "searchValue", "");
         //현재 페이지
         long curPage = HttpUtil.get(request, "curPage", (long)1);
         //게시물 리스트
         List<RecipeInfo> nationList = null;
         //조회 객체
         RecipeInfo search = new RecipeInfo();
         //총 게시물 수
         long totalCount = 0;
         //페이징 객체
         Paging paging = null;
         
         logger.debug("searchType["+ searchType + "]111111111111111111111111111");
         logger.debug("searchValue["+ searchValue + "]2222222222222222222222222");
         
         if(!StringUtil.isEmpty(searchType) || !StringUtil.isEmpty(searchValue))
         {
            search.setSearchType(searchType);
            search.setSearchValue(searchValue);
         }
         
         totalCount = recipeService.recipeListCount(search);
         //totalCount 디버깅용
         logger.debug("===============================");
         logger.debug("totalCount : " + totalCount);
         logger.debug("===============================");
         
         //BoardList호출
         if(totalCount > 0)
         {
            //페이징 객체 생성   
            paging = new Paging("/recipe/recipeSearch", totalCount, LIST_COUNT, PAGE_COUNT, curPage, "curPage");
            
            search.setStartRow(paging.getStartRow());
            search.setEndRow(paging.getEndRow());
            
            nationList = recipeService.recipeNationList(search);
         }
      
         
         
         //jsp에 사용할 모델맵에 담아줌
         model.addAttribute("nationList", nationList);
         model.addAttribute("searchType", searchType);
         model.addAttribute("searchValue", searchValue);
         model.addAttribute("curPage", curPage);
         //모델맵에서 페이징 처리를 해줌
         model.addAttribute("paging",paging);
         
         return "/recipe/recipeSearch";
      }
      
       //레피시 상세조회(INFO, 재료, 조리과정)
       @RequestMapping(value="/recipe/recipeView")
       public String listView(ModelMap model, HttpServletRequest request, HttpServletResponse response)
       {      
         //쿠키값
         String cookieUserId = CookieUtil.getHexValue(request, AUTH_COOKIE_NAME);
         
         //레시피 코드(INFO)
         int recipeCode = HttpUtil.get(request, "recipeCode", (int)0);
         
         //현재 페이지
         long curPage = HttpUtil.get(request, "curPage", (long)1);
      
         //레시피 상세조회(주재료)
         List<RecipeIrdnt> irdntList = null;
         
         //레시피 상세조회(부재료)
         List<RecipeIrdnt> irdntList2 = null;
         
         //레시피 상세조회(INFO)
         RecipeInfo recipeInfo = null;
         
         //레시피 상세조회(조리과정)
         List<RecipeCooking> cookingList = null;
         
         //레시피 댓글 리스트(레시피 코드별)
         List<RecipeReply> replyList = null;
         
         //조회 객체
         RecipeReply search = new RecipeReply();
         
         //총 게시물 수
        long totalCount = 0;
        
        long rcmCount = 0;	
        long recipeBookMarkCount = 0;
        
        //페이징 객체
        Paging paging = null;
        
        //레시피 댓글 카운트(레시피 코드별)
        totalCount = recipeService.recipeReplyListCount(recipeCode);
       
        RecipeRcm recipeRcm = new RecipeRcm();
        BookMark bookMark = new BookMark();
        
        recipeRcm.setUserId(cookieUserId);
        recipeRcm.setRecipeCode(recipeCode);
        
        bookMark.setRecipeCode(recipeCode);
        bookMark.setUserId(cookieUserId);
        
        rcmCount = recipeService.recipeRcmCheck(recipeRcm);
        recipeBookMarkCount = recipeService.recipeBookMarkSelect(bookMark);
        
        System.out.println("=================================");
        System.out.println("rcmCount" + rcmCount);
        System.out.println("recipeBookMarkCount" +recipeBookMarkCount );
        System.out.println();
        System.out.println(totalCount);
        System.out.println("==================================");
        
        
       
        
        if(totalCount > 0)
         {
              //페이징 객체 생성   
            paging = new Paging("/recipe/recipeView", totalCount, LIST_COUNT2, PAGE_COUNT, curPage, "curPage");
            
            search.setRecipeCode(recipeCode);
            search.setStartRow(paging.getStartRow());
            search.setEndRow(paging.getEndRow());   
            
            replyList = recipeService.recipeReplydList(search);
         }
                
                //레시피 상세조회
                if(recipeCode > 0)
                {
                   recipeInfo = recipeService.recipeInfoView(recipeCode);
                   System.out.println("================================");
                   System.out.println(recipeInfo.getRecipeImage());
                   System.out.println("================================");
                   irdntList = recipeService.recipeIrdntView(recipeCode);
                   
                   cookingList = recipeService.recipeCookingView(recipeCode);
                   
                   irdntList2 = recipeService.recipeIrdntView2(recipeCode);
                   
                           
                }
                               
                model.addAttribute("recipeCode", recipeCode);
                model.addAttribute("recipeInfo", recipeInfo);
                model.addAttribute("curPage", curPage);
                model.addAttribute("irdntList", irdntList);
                model.addAttribute("cookingList", cookingList);
                model.addAttribute("irdntList2", irdntList2);
                model.addAttribute("replyList", replyList);
                model.addAttribute("paging",paging);
                model.addAttribute("totalCount",totalCount);
                model.addAttribute("recipeBookMarkCount", recipeBookMarkCount);
                model.addAttribute("rcmCount",rcmCount);
                model.addAttribute("cookieUserId", cookieUserId);
                
                logger.debug("======================================================");
                logger.debug("recipeInfo.getUserId() : " + recipeInfo.getUserId());
                logger.debug("cookieUserId : " + cookieUserId);
                logger.debug("======================================================");
                
                return "/recipe/recipeView";
       }
      
       //레시피 댓글 등록
      @RequestMapping(value="/recipe/recipeReplyProc", method=RequestMethod.POST)
      @ResponseBody
      public Response<Object> recipeReplyProc(MultipartHttpServletRequest request, HttpServletResponse response)
      {
         Response<Object> ajaxResponse = new Response<Object>();
         String cookieUserId = CookieUtil.getHexValue(request, AUTH_COOKIE_NAME);
         
         int recipeCode = HttpUtil.get(request, "recipeCode", (int)0);      //부모글(메인글)의 레시피 코드를 가져옴
         String replyContent = HttpUtil.get(request, "replyContent", "");

         //파라미터 체크
         if(recipeCode > 0 && !StringUtil.isEmpty(replyContent) && !StringUtil.isEmpty(cookieUserId))
         {
            //부모 글에 대한 정보
            RecipeInfo parentRecipeInfo = recipeService.recipeInfoView(recipeCode);
            
            if(parentRecipeInfo != null)
            {
               //게시물 답변은 로그인한 사람의 값을 세팅하기 위해 객체 생성
               RecipeReply recipeReply = new RecipeReply();
               
               recipeReply.setUserId(cookieUserId);
               recipeReply.setReplyContent(replyContent);
               recipeReply.setRecipeCode(recipeCode);

               try 
               {
                  if(recipeService.recipeReplyInsert(recipeReply) > 0)
                  {
                     ajaxResponse.setResponse(0, "success");
                  }
                  else
                  {
                     ajaxResponse.setResponse(500, "internal server error22222");
                  }
               }
               catch(Exception e)
               {
                  logger.error("[RecipeController] recipeReplyProc Exception", e);
                  ajaxResponse.setResponse(500, "internal server error");
               }
            }
            else 
            {
               //부모글이 없을때
               ajaxResponse.setResponse(404, "not found");
            }
         }
         else
         {
            ajaxResponse.setResponse(400, "bad request");
         }
         return ajaxResponse;
      }
      
      //레시피 추천수
      @RequestMapping(value="/recipe/recipeRcmProc", method=RequestMethod.POST)
      @ResponseBody
      public Response<Object> recipeRcmProc(MultipartHttpServletRequest request, HttpServletResponse response)
      {
         Response<Object> ajaxResponse = new Response<Object>();
         String cookieUserId = CookieUtil.getHexValue(request, AUTH_COOKIE_NAME);
         
         int recipeCode = HttpUtil.get(request, "recipeCode", (int)0);      //부모글(메인글)의 레시피 코드를 가져옴
         System.out.println(cookieUserId);
         System.out.println("dfsfffffffffffffffffffffff");
         //추천여부 카운트
         long totalCount = 0;
         
         //파라미터 체크
         if(!StringUtil.isEmpty(cookieUserId))
         {System.out.println("111");
            //해당 레시피에 대한 정보
            RecipeInfo parentRecipeInfo = recipeService.recipeInfoView(recipeCode);
            
            if(parentRecipeInfo != null)
            {
               //로그인한 사람의 값을 세팅하기 위해 객체 생성
               RecipeRcm recipeRcm = new RecipeRcm();
               recipeRcm.setUserId(cookieUserId);
               recipeRcm.setRecipeCode(recipeCode);
               
               //추천여부 카운트
               totalCount = recipeService.recipeRcmCheck(recipeRcm);   
               System.out.println(totalCount);
               System.out.println("=============================");
               //레시피 추천 기록이 없을 때
               if(totalCount <= 0) 
               {      
                  try 
                  {
                     if(recipeService.recipeRcmInsert(recipeRcm) > 0)
                     {
                        recipeService.recipeInfoRcmUpdate(recipeCode);
                        ajaxResponse.setResponse(0, "success");
                     }
                     else
                     {
                        ajaxResponse.setResponse(500, "internal server error22222");
                     }
                  }
                  catch(Exception e)
                  {
                     logger.error("[RecipeController] recipeRcmProc Exception", e);
                     ajaxResponse.setResponse(500, "internal server error");
                  }
               }
               else 
               {
                  //이미 레시피를 추천했을 때
                  recipeService.recipeRcmDelete(recipeRcm);
                  recipeService.recipeInfoRcmUpdateCancel(recipeCode);
                  ajaxResponse.setResponse(401, "not found");
               }
            }
            else 
            {
                //해당 레시피가 없을때
               ajaxResponse.setResponse(404, "not found");
            }
         }
         else
         {
            ajaxResponse.setResponse(400, "bad request");
         }
         
         return ajaxResponse;
      }
   
      //레시피 리스트(추천순)
      @RequestMapping(value="/recipe/bestRecipe2")
      public String listRcm(ModelMap model, HttpServletRequest request, HttpServletResponse response)
      {
         //현재 페이지
         long curPage = HttpUtil.get(request, "curPage", (long)1);
         //게시물 리스트
         List<RecipeInfo> list = null;
         //조회 객체
         RecipeInfo search = new RecipeInfo();
         
         //총 게시물 수
         long totalCount = 0;
         //페이징 객체
         Paging paging = null;
         
         totalCount = recipeService.recipeListCount(search);
         //totalCount 디버깅용
         logger.debug("===============================");
         logger.debug("totalCount : " + totalCount);
         logger.debug("===============================");
         
         //BoardList호출
         if(totalCount > 0)
         {
            //페이징 객체 생성   
            paging = new Paging("/recipe/bestRecipe2", totalCount, LIST_COUNT, PAGE_COUNT, curPage, "curPage");
            
            search.setStartRow(paging.getStartRow());
            search.setEndRow(paging.getEndRow());
            
            list = recipeService.recipeListRcm(search);
         }
         
         //jsp에 사용할 모델맵에 담아줌
         model.addAttribute("list", list);
         model.addAttribute("curPage", curPage);
         //모델맵에서 페이징 처리를 해줌
         model.addAttribute("paging",paging);
         
         return "/recipe/bestRecipe2";
      }
      
      //레시피 즐겨찾기
      @RequestMapping(value="/recipe/recipeBookMarkProc", method=RequestMethod.POST)
      @ResponseBody
      public Response<Object> recipeBookMarkProc(ModelMap model, MultipartHttpServletRequest request, HttpServletResponse response)
      {
         Response<Object> ajaxResponse = new Response<Object>();
         String cookieUserId = CookieUtil.getHexValue(request, AUTH_COOKIE_NAME);
         
         int recipeCode = HttpUtil.get(request, "recipeCode", (int)0);      //부모글(메인글)의 레시피 코드를 가져옴
         
         //즐겨찾기 여부 카운트
         long totalCount = 0;

         //파라미터 체크
         if(!StringUtil.isEmpty(cookieUserId))
         {
            //해당 레시피에 대한 정보
            RecipeInfo parentRecipeInfo = recipeService.recipeInfoView(recipeCode);
            
            if(parentRecipeInfo != null)
            {
               //로그인한 사람의 값을 세팅하기 위해 객체 생성
               BookMark bookMark = new BookMark();
               bookMark.setUserId(cookieUserId);
               bookMark.setRecipeCode(recipeCode);
               
               //즐겨찾기 여부 카운트
               totalCount = recipeService.recipeBookMarkSelect(bookMark);
               System.out.println(totalCount);
               System.out.println("=============================");
               
               //레시피 추천 기록이 없을 때
               if(totalCount <= 0) 
               {      
                  try 
                  {
                     if(recipeService.recipeBookMarkInsert(bookMark) > 0)
                     {      
                        ajaxResponse.setResponse(0, "success");
                     }
                     else
                     {
                        ajaxResponse.setResponse(500, "internal server error22222");
                     }
                  }
                  catch(Exception e)
                  {
                     logger.error("[RecipeController] recipeBookMarkProc Exception", e);
                     ajaxResponse.setResponse(500, "internal server error");
                  }
               }
               else 
               {
                  //이미 즐겨찾기를 했을 때
                  recipeService.recipeBookMarkDelete(bookMark);            
                  ajaxResponse.setResponse(401, "not found");
               }
            }
            else 
            {
                //해당 레시피가 없을때
               ajaxResponse.setResponse(404, "not found");
            }
         }
         else
         {   
            //로그인 안했을 때
            ajaxResponse.setResponse(400, "bad request");
         }
         
         return ajaxResponse;
      }
      
          //레피시 등록 페이지
          @RequestMapping(value="/recipe/recipeSave")
          public String recipeSave(ModelMap model, HttpServletRequest request, HttpServletResponse response)
          {      
             return "/recipe/recipeSave";
          }
          
        //나만의 레시피 등록
          @RequestMapping(value="/recipe/recipeSaveProc", method=RequestMethod.POST)
          @ResponseBody
          public Response<Object> recipeProc(MultipartHttpServletRequest request, HttpServletResponse response)
          {
             Response<Object> ajaxResponse = new Response<Object>();
             try 
             {
             String cookieUserId = CookieUtil.getHexValue(request, AUTH_COOKIE_NAME);
             String myRecipeTitle = HttpUtil.get(request, "myRecipeTitle");
             String myRecipeIntro = HttpUtil.get(request, "myRecipeIntro");
             int myRecipeNation = HttpUtil.get(request, "myRecipeNation", 0);
             int myRecipeFood = HttpUtil.get(request, "myRecipeFood", 0);
             String myRecipeQnt = HttpUtil.get(request, "myRecipeQnt");
             String myRecipeTime = HttpUtil.get(request, "myRecipeTime");
             
             String mainIrdntCount = HttpUtil.get(request,"mainIrdntCount");
             String subIrdntCount = HttpUtil.get(request,"subIrdntCount");
             String cookingCount = HttpUtil.get(request,"cookingCount");
             
             int main = Integer.parseInt(mainIrdntCount);
             int sub = Integer.parseInt(subIrdntCount);
             int cooking = Integer.parseInt(cookingCount);
          
             int seq = recipeSaveDao.recipeMaxNum()+1;
             String path = UPLOAD_SAVE_DIR +"\\"+seq;
             User user = null;
             
             try
             {
                for(int i =1; i<=main;i++)
                {
                   Integer.parseInt(HttpUtil.get(request,"mainIrdntAmt"+i));
                }
               
             }
             catch(NumberFormatException e)
             {
                ajaxResponse.setResponse(1000,"type error");
                return ajaxResponse;
             }
             
             if(!StringUtil.isEmpty(myRecipeTitle) && !StringUtil.isEmpty(myRecipeIntro) && 
                !StringUtil.isEmpty(myRecipeNation) && !StringUtil.isEmpty(myRecipeFood) && 
                !StringUtil.isEmpty(myRecipeQnt) && !StringUtil.isEmpty(myRecipeTime))
             {
                
                if(userService.userSelect(cookieUserId) != null)
                
                {
                   //사용자 아이디가 있으면 정상적으로 등록
                   
                   RecipeInfo recipeInfo = new RecipeInfo();
                   
                   recipeInfo.setRecipeCode(seq);
                   recipeInfo.setUserId(cookieUserId);
                   recipeInfo.setRecipeName(myRecipeTitle);
                   recipeInfo.setRecipeIntro(myRecipeIntro);
                   recipeInfo.setNationCode(myRecipeNation);
                   recipeInfo.setFoodCode(myRecipeFood);
                   recipeInfo.setRecipeQnt(myRecipeQnt);
                   recipeInfo.setRecipeTime(myRecipeTime);
                   
                   File file = new File(path);
                     
                   if (!file.exists()) 
                   {
                      if (file.mkdir() == true)
                      {
                         System.out.println("디렉토리가 생성되었습니다.");
                      } 
                      else 
                      {
                         System.out.println("디렉토리를 생성하지 못했습니다.");
                      } 
                   } 
                   else 
                   {
                      System.out.println("디렉토리가 존재합니다.");
                   }
                   
                   FileData fileData = HttpUtil.getFile(request,"recipeInfoFile",path,"mainIrdnt");
                   String imageDrectory = "\\resources\\upload\\" + seq + "\\"+fileData.getFileName();
                   recipeInfo.setRecipeImage(imageDrectory);
                   
                   if(recipeService.insertInfo(recipeInfo)>0)
                   {
                      
                       
                   }
                   else
                   {
                      ajaxResponse.setResponse(301, "insertRecipeInfo Error");
                      return ajaxResponse;
                   }
                   
                   try
                   {
                      
                      for(int i =1 ; i <= main ; i ++)
                      {
                         RecipeIrdnt mainIrdnt = new RecipeIrdnt();
                         mainIrdnt.setIrdntAmount(HttpUtil.get(request,"mainIrdntAmt"+i)+"g");
                         mainIrdnt.setIrdntName(HttpUtil.get(request,"mainIrdntNm"+i));
                         mainIrdnt.setIrdntTypeCode(3060001);
                         mainIrdnt.setRecipeCode(seq);
         
                         if(recipeService.insertIrdnt(mainIrdnt) > 0)
                         {
                            ajaxResponse.setResponse(0,"success");
                         }
                         else
                         {
                           ajaxResponse.setResponse(302,"mainInsert Error"); 
                           return ajaxResponse;
                         }
                      }
                      
                      for(int i=1; i <= sub; i++)
                      {
                         RecipeIrdnt subIrdnt = new RecipeIrdnt();
                         subIrdnt.setIrdntAmount(HttpUtil.get(request,"subIrdntAmt"+i));
                         subIrdnt.setIrdntName(HttpUtil.get(request,"subIrdntNm"+i));
                         subIrdnt.setIrdntTypeCode(3060002);
                         subIrdnt.setRecipeCode(seq);
                         
                         if(recipeService.insertIrdnt(subIrdnt) > 0)
                         {
                            ajaxResponse.setResponse(0,"success");
                         }
                         else
                         {
                           ajaxResponse.setResponse(303,"subInsert Error"); 
                           return ajaxResponse;
                         }
                         
                      }
                      
                  }
                   catch(NumberFormatException e)
                  {
                     ajaxResponse.setResponse(300,"irdnt Input error"); 
                      return ajaxResponse;
                   }
                   
                   
                   
                   path = path + "\\cooking";
                   File cookingfile = new File(path);
                     
                   if (!cookingfile.exists()) 
                   {
                      if (cookingfile.mkdir() == true)
                      {
                         System.out.println("디렉토리가 생성되었습니다.");
                      } 
                      else 
                      {
                         System.out.println("디렉토리를 생성하지 못했습니다.");
                      }
                   } 
                   else 
                   {
                      System.out.println("디렉토리가 존재합니다.");
                   }
                   for(int i = 1; i<=cooking; i++)
                   {
                       RecipeCooking recipeCooking = new RecipeCooking();
                       
                       path = UPLOAD_SAVE_DIR +"\\"+seq +"\\cooking";
                       
                       FileData fileData2 = HttpUtil.getFile(request,"cookingFile"+i,path,"step"+i);
                       
                      path = "\\resources\\upload\\" + seq +"\\cooking\\"+fileData2.getFileName();
                       
                      recipeCooking.setCookingExplan(HttpUtil.get(request,"stepText"+i));
                      recipeCooking.setCookingImage(path);
                      recipeCooking.setCookingOrder(i);
                      recipeCooking.setRecipeCode(seq);
                      
                      if(recipeService.insertCooking(recipeCooking)>0)
                      {
                         ajaxResponse.setResponse(0,"success");
                      }
                      else
                      {
                         ajaxResponse.setResponse(304,"cookingInsertError");
                         return ajaxResponse;
                      }
                   }
                }
                else
                {
                   ajaxResponse.setResponse(100, "Not ID");
                   return ajaxResponse;
                }
             }
             else
             {
                   //가입된 아이디가 없는 경우
                ajaxResponse.setResponse(200, "input Empty error");
                return ajaxResponse;
             }
             }
             catch(NullPointerException e)
             {
                ajaxResponse.setResponse(001,"Null");
             }
             return ajaxResponse;      
          } 
          
              
        //유저 등록 레시피 삭제
  		@RequestMapping(value="/recipe/recipeDelete", method=RequestMethod.POST)
  		@ResponseBody	
  		public Response<Object> recipeDelete(HttpServletRequest request, HttpServletResponse response)
  		{
  			Response<Object> ajaxResponse = new Response<Object>();
  				
  			String cookieUserId = CookieUtil.getHexValue(request, AUTH_COOKIE_NAME);
  			int recipeCode = HttpUtil.get(request, "recipeCode", (int)0);
  			
  			RecipeInfo recipeInfo = recipeInfoDao.recipeInfoSelect(recipeCode);
  			if(recipeCode > 0)
  			{
  				
  				if(cookieUserId != null)
  				{
  					//게시글이 존재할 때 게시글 작성자와 로그인 사용자가 같은지 체크(로그인 한 사용자 본인 게시글이 맞는지 체크)
  					if(StringUtil.equals(recipeInfo.getUserId(), cookieUserId))
  					{
  						try
  						{
  							//답글의 유무를 확인(답글이 있을 경우 삭제를 못하게 할려고 함)
  							if(recipeService.recipeReplyListCount(recipeCode) > 0)
  							{
  								ajaxResponse.setResponse(-999, "answers exist and cannot be deleted");
  							}
  							else
  							{
  								//답글이 없는 경우
  								if(recipeService.deleteRecipe(recipeCode) > 0)	
  								{
  									ajaxResponse.setResponse(0, "success");	
  								}
  								else
  								{
  									ajaxResponse.setResponse(500, "server error222");
  								}
  							}
  						}
  						catch(Exception e)
  						{
  							logger.error("[RecipeController] recipeDelete Exception", e);
  							ajaxResponse.setResponse(500, "server error");
  						}
  					}
  					else
  					{
  						//내가 등록한 레시피가 아닐 경우
  						ajaxResponse.setResponse(403, "server error");
  					}
  					
  				}
  				else
  				{
  					//게시물이 존재하지 않을 때
  					ajaxResponse.setResponse(404, "not found");
  				}
  			}
  			else
  			{
  				ajaxResponse.setResponse(400, "bad Request");
  			}
  			
  			return ajaxResponse;
  		}
               
        //레시피 댓글 삭제
  		@RequestMapping(value="/recipe/replyDelete", method=RequestMethod.POST)
  		@ResponseBody	
  		public Response<Object> replyDelete(MultipartHttpServletRequest request, HttpServletResponse response)
  		{
  			Response<Object> ajaxResponse = new Response<Object>();	
  			String cookieUserId = CookieUtil.getHexValue(request, AUTH_COOKIE_NAME);
  			int recipeCode = HttpUtil.get(request, "recipeCode", (int)0);
  			long recipeReplySeq = HttpUtil.get(request, "recipeReplySeq", (long)0);
  			String recipeReplyUserId = HttpUtil.get(request, "recipeReplyUserId");
  			
  			 System.out.println("111111111111111111111111111111111111111111111111111111");
  			 System.out.println("ReplyUserId : " + recipeReplyUserId);
  			 System.out.println("111111111111111111111111111111111111111111111111111111");
  			 System.out.println("recipeCode : " + recipeCode);
 			 System.out.println("111111111111111111111111111111111111111111111111111111");
 			 System.out.println("recipeReplySeq : " + recipeReplySeq);
 			 System.out.println("111111111111111111111111111111111111111111111111111111");

  	        RecipeReply recipeReply = recipeService.replySelect(recipeReplySeq);
  	        
  			//댓글이 있을 때
  			if(recipeReply != null)
  			{
  				//쿠키값이 있을 때
  				if(cookieUserId != null)
  				{	
  					//댓글 작성자와 로그인 사용자가 같은지 체크(로그인 한 사용자 본인 게시글이 맞는지 체크)
  					if(StringUtil.equals(recipeReplyUserId, cookieUserId))
  					{					
  							if(recipeService.replyDelete(recipeReplySeq) > 0)
  							{
  								ajaxResponse.setResponse(0, "success");	
  							}
  							else	
  							{	
  								ajaxResponse.setResponse(500, "server error222");
  							}		
  					}
  					else
  					{
  						//내가 등록한 댓글이 아닐 경우
  						ajaxResponse.setResponse(403, "server error");
  					}
  				}
  				else
  				{
  					//로그인이 안되어 있을 때
  					ajaxResponse.setResponse(404, "not found");
  				}
  			}
  			else
  			{	//댓글이 존재하지 않을 때
  				ajaxResponse.setResponse(400, "bad Request");
  			}
  
  			return ajaxResponse;
  		}
          
}