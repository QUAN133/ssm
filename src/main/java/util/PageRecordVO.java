package main.java.util;

public class PageRecordVO {
	private int pageNum;
	private int numInPage;
	public int getPageNum() {
		return (pageNum-1)*numInPage;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	public int getNumInPage() {
		return numInPage;
	}
	public void setNumInPage(int numInPage) {
		this.numInPage = numInPage;
	}
	@Override
	public String toString() {
		return "PageRecordVO [pageNum=" + pageNum + ", numInPage=" + numInPage + "]";
	}
	
}
