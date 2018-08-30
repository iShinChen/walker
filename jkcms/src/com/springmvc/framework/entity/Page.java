package com.springmvc.framework.entity;

public class Page {

	/**
	 * 当前页数
	 */
	private int currPage = 1;

	/**
	 * 每页多少行
	 */
	private int pageRow = 20;

	/**
	 * 总页数
	 */
	private int totalPage;

	/**
	 * 总共多少行
	 */
	private int totalRow;

	/**
	 * 开始行
	 */
	private int startRow;


	/**
	 * 结束行
	 */
	private int endRow;

	public int getTotalPage() {
		return totalPage;
	}

	public int getCurrPage() {
		return currPage;
	}

	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}

	public int getPageRow() {
		return pageRow;
	}

	public void setPageRow(int pageRow) {
		this.pageRow = pageRow;
	}

	public int getTotalRow() {
		return totalRow;
	}

	public void setTotalRow(int totalRow) {
		totalPage = totalRow % this.pageRow > 0 ? totalRow / this.pageRow + 1
				: totalRow / this.pageRow;
		this.endRow = this.startRow + this.pageRow > totalRow ? totalRow
				: this.startRow + this.pageRow;
		this.totalRow = totalRow;
	}

	public int getStartRow() {
		return startRow;
	}
	
	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}


	public int getEndRow() {
		return endRow;
	}
}
