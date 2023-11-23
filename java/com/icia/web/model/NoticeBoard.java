package com.icia.web.model;

import java.io.Serializable;

public class NoticeBoard implements Serializable
{
   private static final long serialVersionUID = 1L;
   
   private long noticeSeq;               //공지사항 번호
   private String adminId;               //관리자 아이디
   private String noticeTitle;            //공지사항 제목
   private String noticeContent;         //공지사항 내용
   private String regDate;               //등록일
   private int noticeIndent;         //게시물 들여쓰기 
   private String adminName;
   
   private long startRow;               //시작 rownum
   private long endRow;               //끝 rownum
   
   public NoticeBoard()
   {
      noticeSeq = 0;
      adminId = "";
      noticeTitle = "";
      noticeContent = "";
      regDate = "";
      startRow = 0;
      endRow = 0;
      noticeIndent = 0;
      adminName = "";
   }

   public long getNoticeSeq() {
      return noticeSeq;
   }

   public void setNoticeSeq(long noticeSeq) {
      this.noticeSeq = noticeSeq;
   }

   public String getAdminName() {
      return adminName;
   }

   public void setAdminName(String adminName) {
      this.adminName = adminName;
   }

   public String getRegDate() {
      return regDate;
   }

   public int getNoticeIndent() {
      return noticeIndent;
   }

   public void setNoticeIndent(int noticeIndent) {
      this.noticeIndent = noticeIndent;
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

   public void setRegDate(String regDate) {
      this.regDate = regDate;
   }

   public String getAdminId() {
      return adminId;
   }

   public void setAdminId(String adminId) {
      this.adminId = adminId;
   }

   public String getNoticeTitle() {
      return noticeTitle;
   }

   public void setNoticeTitle(String noticeTitle) {
      this.noticeTitle = noticeTitle;
   }

   public String getNoticeContent() {
      return noticeContent;
   }

   public void setNoticeContent(String noticeContent) {
      this.noticeContent = noticeContent;
   }
   
}