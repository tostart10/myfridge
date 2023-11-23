package com.icia.web.dao;
import java.util.*;

import org.springframework.stereotype.Repository;

import com.icia.web.model.*;

@Repository("recipeSave")
public interface RecipeSaveDao 
{
   //SEQ가장 큰 값 가져오기
   public int recipeMaxNum();
   
   //마이 레시피 등록(INFO)
   public int insertInfo(RecipeInfo recipeInfo);
   public int insertCooking(RecipeCooking recipeCooking);
   public int insertIrdnt(RecipeIrdnt recipeIrdnt);
   
   //마이 레시피 삭제
   public int deleteInfo(int recipeCode);
   public int deleteIrdnt(int recipeCode);
   public int deleteCooking(int recipeCode);
}