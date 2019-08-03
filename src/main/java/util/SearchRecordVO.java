package main.java.util;

public class SearchRecordVO {
	private String type;
	private String startTime;
	private String endTime;
	private String sort;
	private int pageNum;
	private int numInPage;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
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
	
}
