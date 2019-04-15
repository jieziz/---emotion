package com.jiezi.emotion.business.mood.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.jiezi.emotion.base.BaseEntity;
import com.jiezi.emotion.sys.user.entity.User;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户心情，用于用户自己心情记录（类似于文章，说说，朋友圈功能）
 * @author zhangliang
 *
 */
@Entity
@Table(name = "business_mood")
@Setter
@Getter
public class Mood extends BaseEntity<Long>{
	
	
	/**
	 * 用户id
	 */
	@Column(name = "user_id")
	private Long userId;
	
	/**
	 * 用户
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
	private User user;
	
	/**
	 * 标题
	 */
	@Column(name = "title")
	private String title;
		
	/**
	 * 内容
	 */
	@Column(name = "content")
	private String content;
	
	
	/**
	 * 类型（对于心情的分类）
	 */
	@Column(name = "type")
	private Integer type;
	
	/**
	 * 阅读点击量
	 */
	@Column(name = "read_count")
	private Integer readCount;
	
	/**
	 * 首页心情文字展示（不填默认正文内容）
	 */
	@Column(name = "index_content")
	private String indexContent;
	
	
	/**
	 * (过滤参数)用户姓名
	 */
	@Transient
	private String username;
	
	
	

}
