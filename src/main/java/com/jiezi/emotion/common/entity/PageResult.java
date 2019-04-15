package com.jiezi.emotion.common.entity;

import java.util.List;

import org.springframework.data.domain.Page;

/**
 * 分页搜索的结果bean
 * @author zhangliang
 *
 */
public class PageResult<T> {
	private Long total;
	private List<T> rows;
	private Integer totalPages;
	private Integer pageNum;
	public PageResult(){
		
	}
	public PageResult(Page<T> pageResult,PageParameter page){
		setRows(pageResult.getContent());
		setTotal(pageResult.getTotalElements());
		setTotalPages(pageResult.getTotalPages());
		setPageNum(page.getPage());
		
	}
	

	public Integer getPageNum() {
		return pageNum;
	}
	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
	public List<T> getRows() {
		return rows;
	}
	public void setRows(List<T> rows) {
		this.rows = rows;
	}
	
	

}
